package br.gov.aposentadoria.resource;

import br.gov.aposentadoria.model.PeriodoServico;
import br.gov.aposentadoria.model.PeriodoTempo;
import br.gov.aposentadoria.model.Usuario;
import br.gov.aposentadoria.repository.PeriodoServicoRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;

@Path("/api/periodos-servico")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Períodos de Serviço", description = "Gerenciamento de períodos de serviço para cálculo de tempo de contribuição")
public class PeriodoServicoResource {

    @Inject
    PeriodoServicoRepository periodoServicoRepository;

    @GET
    @Path("/usuario/{id}")
    @Operation(summary = "Listar períodos de serviço", description = "Lista todos os períodos de serviço de um usuário")
    public Response listarPeriodosPorUsuario(@PathParam("id") Long usuarioId) {
        Usuario usuario = Usuario.findById(usuarioId);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuário não encontrado")
                    .build();
        }
        
        List<PeriodoServico> periodos = PeriodoServico.list("usuario.id", usuarioId);
        return Response.ok(periodos).build();
    }
    
    @POST
    @Transactional
    @Operation(summary = "Adicionar período de serviço", description = "Adiciona um novo período de serviço para um usuário")
    public Response adicionarPeriodo(@Valid PeriodoServicoRequest request) {
        Usuario usuario = Usuario.findById(request.usuarioId);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuário não encontrado")
                    .build();
        }
        
        // Validar datas
        if (request.dataInicio.isAfter(request.dataFim)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Data de início deve ser anterior à data de fim")
                    .build();
        }
        
        PeriodoServico periodo = new PeriodoServico();
        periodo.usuario = usuario;
        periodo.dataInicio = request.dataInicio;
        periodo.dataFim = request.dataFim;
        periodo.orgaoEmpregador = request.orgaoEmpregador;
        periodo.tipoServico = request.tipoServico;
        periodo.cargo = request.cargo;
        periodo.numeroPortaria = request.numeroPortaria;
        periodo.insalubridade = request.insalubridade != null ? request.insalubridade : false;
        
        PeriodoServico periodoSalvo = periodoServicoRepository.adicionarPeriodo(periodo);
        
        return Response.status(Response.Status.CREATED)
                .entity(periodoSalvo)
                .build();
    }
    
    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Atualizar período de serviço", description = "Atualiza um período de serviço existente")
    public Response atualizarPeriodo(
            @PathParam("id") Long periodoId,
            @Valid PeriodoServicoRequest request) {
        
        PeriodoServico periodo = PeriodoServico.findById(periodoId);
        if (periodo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Período de serviço não encontrado")
                    .build();
        }
        
        // Validar datas
        if (request.dataInicio.isAfter(request.dataFim)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Data de início deve ser anterior à data de fim")
                    .build();
        }
        
        periodo.dataInicio = request.dataInicio;
        periodo.dataFim = request.dataFim;
        periodo.orgaoEmpregador = request.orgaoEmpregador;
        periodo.tipoServico = request.tipoServico;
        periodo.cargo = request.cargo;
        periodo.numeroPortaria = request.numeroPortaria;
        periodo.insalubridade = request.insalubridade != null ? request.insalubridade : false;
        
        // Verificar concomitância novamente
        boolean isConcomitante = periodoServicoRepository.verificarConcomitancia(periodo);
        periodo.concomitante = isConcomitante;
        
        periodo.persist();
        
        return Response.ok(periodo).build();
    }
    
    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Remover período de serviço", description = "Remove um período de serviço existente")
    public Response removerPeriodo(@PathParam("id") Long periodoId) {
        PeriodoServico periodo = PeriodoServico.findById(periodoId);
        if (periodo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Período de serviço não encontrado")
                    .build();
        }
        
        periodo.delete();
        
        return Response.noContent().build();
    }
    
    @POST
    @Path("/{id}/converter-tempo")
    @Transactional
    @Operation(summary = "Converter tempo especial", description = "Aplica fatores de conversão de tempo especial para tempo comum")
    public Response converterTempoEspecial(@PathParam("id") Long periodoId) {
        PeriodoServico periodo = PeriodoServico.findById(periodoId);
        if (periodo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Período de serviço não encontrado")
                    .build();
        }
        
        if (!periodoServicoRepository.podeTerConversaoTempo(periodo)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Período não é elegível para conversão de tempo")
                    .build();
        }
        
        PeriodoServico periodoConvertido = periodoServicoRepository.aplicarConversaoTempo(periodo);
        
        return Response.ok(periodoConvertido).build();
    }
    
    @GET
    @Path("/usuario/{id}/tempo-total")
    @Operation(summary = "Calcular tempo total", description = "Calcula o tempo total de serviço não concomitante de um usuário")
    public Response calcularTempoTotal(@PathParam("id") Long usuarioId) {
        Usuario usuario = Usuario.findById(usuarioId);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuário não encontrado")
                    .build();
        }
        
        List<PeriodoServico> periodos = periodoServicoRepository.listarPeriodosNaoConcomitantes(usuario);
        
        PeriodoTempo tempoTotal = periodos.stream()
                .map(PeriodoServico::calcularTempoComConversao)
                .reduce(new PeriodoTempo(0, 0, 0), PeriodoTempo::somar);
        
        return Response.ok(new TempoTotalResponse(tempoTotal)).build();
    }
    
    public static class PeriodoServicoRequest {
        public Long usuarioId;
        public LocalDate dataInicio;
        public LocalDate dataFim;
        public String orgaoEmpregador;
        public PeriodoServico.TipoServico tipoServico;
        public String cargo;
        public String numeroPortaria;
        public Boolean insalubridade;
    }
    
    public static class TempoTotalResponse {
        public int anos;
        public int meses;
        public int dias;
        public String tempoFormatado;
        
        public TempoTotalResponse(PeriodoTempo periodoTempo) {
            this.anos = periodoTempo.anos();
            this.meses = periodoTempo.meses();
            this.dias = periodoTempo.dias();
            this.tempoFormatado = periodoTempo.toString();
        }
    }
}
