package br.gov.aposentadoria.resource;

import br.gov.aposentadoria.model.PeriodoServico;
import br.gov.aposentadoria.model.Simulacao;
import br.gov.aposentadoria.model.Usuario;
import br.gov.aposentadoria.service.SimuladorService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;

@Path("/api/simulador")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulador de Aposentadoria", description = "API para simulação de aposentadoria")
public class SimuladorResource {

    @Inject
    SimuladorService simuladorService;

    @GET
    @Operation(summary = "Endpoint de teste", description = "Verifica se o serviço está disponível")
    public Response hello() {
        return Response.ok("Simulador de Aposentadoria funcionando!").build();
    }
    
    @POST
    @Path("/executar")
    @Operation(summary = "Executar simulação", description = "Executa uma simulação de aposentadoria com base nos parâmetros fornecidos")
    @Transactional
    public Response executarSimulacao(
            @Valid SimulacaoRequest request) {
        
        // Buscar usuário
        Usuario usuario = Usuario.findById(request.usuarioId);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuário não encontrado")
                    .build();
        }
        
        // Buscar períodos de serviço
        List<PeriodoServico> periodosServico = PeriodoServico.list("usuario.id", usuario.id);
        
        // Data de referência (hoje se não fornecida)
        LocalDate dataReferencia = request.dataReferencia != null 
                ? request.dataReferencia 
                : LocalDate.now();
        
        // Executar simulação
        Simulacao simulacao = simuladorService.executarSimulacao(
                usuario, 
                periodosServico, 
                request.regraAposentadoria,
                dataReferencia);
        
        if (request.nomeSimulacao != null && !request.nomeSimulacao.isEmpty()) {
            simulacao.nomeSimulacao = request.nomeSimulacao;
            simulacao.persist();
        }
        
        return Response.ok(simulacao).build();
    }
    
    @GET
    @Path("/usuario/{id}/simulacoes")
    @Operation(summary = "Listar simulações", description = "Lista todas as simulações realizadas por um usuário")
    public Response listarSimulacoes(@PathParam("id") Long usuarioId) {
        Usuario usuario = Usuario.findById(usuarioId);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuário não encontrado")
                    .build();
        }
        
        List<Simulacao> simulacoes = Simulacao.list("usuario.id", usuarioId);
        return Response.ok(simulacoes).build();
    }
    
    @GET
    @Path("/simulacao/{id}")
    @Operation(summary = "Buscar simulação", description = "Busca uma simulação pelo ID")
    public Response buscarSimulacao(@PathParam("id") Long simulacaoId) {
        Simulacao simulacao = Simulacao.findById(simulacaoId);
        if (simulacao == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Simulação não encontrada")
                    .build();
        }
        
        return Response.ok(simulacao).build();
    }
    
    public static class SimulacaoRequest {
        public Long usuarioId;
        public String nomeSimulacao;
        public Simulacao.RegraAposentadoria regraAposentadoria;
        public LocalDate dataReferencia;
    }
}
