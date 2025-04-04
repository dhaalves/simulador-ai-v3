package br.gov.aposentadoria.repository;

import br.gov.aposentadoria.model.PeriodoServico;
import br.gov.aposentadoria.model.PeriodoTempo;
import br.gov.aposentadoria.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class PeriodoServicoRepository implements PanacheRepository<PeriodoServico> {
    
    /**
     * Lista todos os períodos de serviço não concomitantes de um usuário
     */
    public List<PeriodoServico> listarPeriodosNaoConcomitantes(Usuario usuario) {
        return list("usuario = ?1 and concomitante = false", usuario);
    }
    
    /**
     * Adiciona um novo período de serviço
     */
    public PeriodoServico adicionarPeriodo(PeriodoServico periodoServico) {
        // Verificar se o período é concomitante com outros períodos
        boolean isConcomitante = verificarConcomitancia(periodoServico);
        periodoServico.concomitante = isConcomitante;
        
        persist(periodoServico);
        return periodoServico;
    }
    
    /**
     * Verifica se um período é concomitante com outros períodos já registrados
     */
    public boolean verificarConcomitancia(PeriodoServico novoPeriodo) {
        List<PeriodoServico> periodosExistentes = list(
            "usuario = ?1 and id != ?2", 
            novoPeriodo.usuario, 
            novoPeriodo.id != null ? novoPeriodo.id : -1
        );
        
        for (PeriodoServico periodoExistente : periodosExistentes) {
            // Se há sobreposição de datas, é concomitante
            if (!(novoPeriodo.dataFim.isBefore(periodoExistente.dataInicio) || 
                  novoPeriodo.dataInicio.isAfter(periodoExistente.dataFim))) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Verifica se é possível aplicar conversão de tempo especial (insalubre)
     */
    public boolean podeTerConversaoTempo(PeriodoServico periodo) {
        return periodo.insalubridade && 
               periodo.tipoServico != PeriodoServico.TipoServico.INSALUBRE &&
               !periodo.tempoConvertido;
    }
    
    /**
     * Aplica conversão de tempo especial (insalubre) para tempo comum
     */
    public PeriodoServico aplicarConversaoTempo(PeriodoServico periodo) {
        if (!podeTerConversaoTempo(periodo)) {
            return periodo;
        }
        
        // Fator de conversão: 1.4 para homens e 1.2 para mulheres
        Usuario usuario = periodo.usuario;
        double fator = "M".equals(usuario.sexo) ? 1.4 : 1.2;
        
        periodo.tempoConvertido = true;
        periodo.fatorConversao = fator;
        
        persist(periodo);
        return periodo;
    }
    
    /**
     * Calcula o tempo total de serviço em uma determinada categoria
     */
    public PeriodoTempo calcularTempoTotal(Usuario usuario, PeriodoServico.TipoServico tipoServico) {
        List<PeriodoServico> periodos = list(
            "usuario = ?1 and tipoServico = ?2 and concomitante = false", 
            usuario, 
            tipoServico
        );
        
        return periodos.stream()
                .map(PeriodoServico::calcularTempoComConversao)
                .reduce(new PeriodoTempo(0, 0, 0), PeriodoTempo::somar);
    }
    
    /**
     * Calcula o tempo de serviço até uma data específica
     */
    public PeriodoTempo calcularTempoAteData(Usuario usuario, LocalDate dataLimite) {
        List<PeriodoServico> periodos = list(
            "usuario = ?1 and dataInicio <= ?2 and concomitante = false", 
            usuario, 
            dataLimite
        );
        
        return periodos.stream()
                .map(p -> {
                    // Se o período termina após a data limite, ajustamos a data fim
                    if (p.dataFim.isAfter(dataLimite)) {
                        LocalDate dataFimOriginal = p.dataFim;
                        p.dataFim = dataLimite;
                        PeriodoTempo resultado = p.calcularTempoComConversao();
                        p.dataFim = dataFimOriginal; // restauramos o valor original
                        return resultado;
                    } else {
                        return p.calcularTempoComConversao();
                    }
                })
                .reduce(new PeriodoTempo(0, 0, 0), PeriodoTempo::somar);
    }
}
