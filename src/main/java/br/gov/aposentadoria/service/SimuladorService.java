package br.gov.aposentadoria.service;

import br.gov.aposentadoria.model.PeriodoServico;
import br.gov.aposentadoria.model.PeriodoTempo;
import br.gov.aposentadoria.model.Simulacao;
import br.gov.aposentadoria.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@ApplicationScoped
public class SimuladorService {

    /**
     * Calcula o tempo total de contribuição de um usuário
     */
    public PeriodoTempo calcularTempoContribuicao(List<PeriodoServico> periodosServico) {
        return periodosServico.stream()
                .filter(p -> !p.concomitante) // Filtra períodos concomitantes
                .map(PeriodoServico::calcularTempoComConversao)
                .reduce(new PeriodoTempo(0, 0, 0), PeriodoTempo::somar);
    }
    
    /**
     * Calcula o tempo de serviço público
     */
    public PeriodoTempo calcularTempoServicoPublico(List<PeriodoServico> periodosServico) {
        return periodosServico.stream()
                .filter(p -> !p.concomitante) // Filtra períodos concomitantes
                .filter(p -> {
                    switch (p.tipoServico) {
                        case ESTATUTARIO:
                        case SERVICO_PUBLICO_FEDERAL:
                        case SERVICO_PUBLICO_ESTADUAL:
                        case SERVICO_PUBLICO_MUNICIPAL:
                            return true;
                        default:
                            return false;
                    }
                })
                .map(PeriodoServico::calcularTempoComConversao)
                .reduce(new PeriodoTempo(0, 0, 0), PeriodoTempo::somar);
    }
    
    /**
     * Calcula o tempo no cargo atual
     */
    public PeriodoTempo calcularTempoCargo(Usuario usuario, List<PeriodoServico> periodosServico) {
        if (usuario.cargoAtual == null || usuario.cargoAtual.isEmpty()) {
            return new PeriodoTempo(0, 0, 0);
        }
        
        return periodosServico.stream()
                .filter(p -> !p.concomitante) // Filtra períodos concomitantes
                .filter(p -> p.cargo != null && p.cargo.equals(usuario.cargoAtual))
                .map(PeriodoServico::calcularTempoComConversao)
                .reduce(new PeriodoTempo(0, 0, 0), PeriodoTempo::somar);
    }
    
    /**
     * Calcula a idade do usuário em uma data específica
     */
    public int calcularIdade(Usuario usuario, LocalDate data) {
        return Period.between(usuario.dataNascimento, data).getYears();
    }
    
    /**
     * Calcula a pontuação (idade + tempo de contribuição) do usuário
     */
    public int calcularPontuacao(Usuario usuario, PeriodoTempo tempoContribuicao, LocalDate dataReferencia) {
        int idade = calcularIdade(usuario, dataReferencia);
        int pontos = idade + tempoContribuicao.anos();
        
        // Adicionar meses e dias convertidos para fração de ano
        double mesesEmAnos = tempoContribuicao.meses() / 12.0;
        double diasEmAnos = tempoContribuicao.dias() / 365.0;
        
        pontos += Math.round((mesesEmAnos + diasEmAnos) * 100) / 100.0;
        
        return pontos;
    }
    
    /**
     * Calcula o percentual do benefício baseado no tempo de contribuição
     */
    public double calcularPercentualBeneficio(PeriodoTempo tempoContribuicao) {
        // Regra: 60% para os primeiros 20 anos + 2% para cada ano adicional
        if (tempoContribuicao.anos() < 20) {
            return 60.0;
        }
        
        int anosAdicional = tempoContribuicao.anos() - 20;
        return 60.0 + (anosAdicional * 2.0);
    }
    
    /**
     * Verifica se o usuário é elegível para a regra permanente de aposentadoria
     */
    public boolean verificarElegibilidadeRegraPermanente(Usuario usuario, 
                                                       PeriodoTempo tempoContribuicao,
                                                       PeriodoTempo tempoServicoPublico,
                                                       PeriodoTempo tempoCargo,
                                                       LocalDate dataReferencia) {
        int idade = calcularIdade(usuario, dataReferencia);
        boolean idadeMinima = "M".equals(usuario.sexo) ? idade >= 65 : idade >= 62;
        boolean tempoContribuicaoMinimo = tempoContribuicao.anos() >= 25;
        boolean tempoServicoPublicoMinimo = tempoServicoPublico.anos() >= 10;
        boolean tempoCargoMinimo = tempoCargo.anos() >= 5;
        
        return idadeMinima && tempoContribuicaoMinimo && tempoServicoPublicoMinimo && tempoCargoMinimo;
    }
    
    /**
     * Verifica se o usuário é elegível para a regra de transição por pedágio
     */
    public boolean verificarElegibilidadeRegraTransicaoPedagio(Usuario usuario,
                                                             PeriodoTempo tempoContribuicao,
                                                             PeriodoTempo tempoServicoPublico,
                                                             PeriodoTempo tempoCargo,
                                                             LocalDate dataReferencia,
                                                             PeriodoTempo tempoPedagio) {
        int idade = calcularIdade(usuario, dataReferencia);
        boolean idadeMinima;
        boolean tempoContribuicaoMinimo;
        
        if ("M".equals(usuario.sexo)) {
            idadeMinima = idade >= 60;
            tempoContribuicaoMinimo = tempoContribuicao.anos() >= 35;
        } else {
            idadeMinima = idade >= 57;
            tempoContribuicaoMinimo = tempoContribuicao.anos() >= 30;
        }
        
        boolean tempoServicoPublicoMinimo = tempoServicoPublico.anos() >= 20;
        boolean tempoCargoMinimo = tempoCargo.anos() >= 5;
        boolean pedagioCumprido = tempoPedagio != null && tempoPedagio.anos() == 0 && 
                                 tempoPedagio.meses() == 0 && tempoPedagio.dias() == 0;
        
        return idadeMinima && tempoContribuicaoMinimo && tempoServicoPublicoMinimo && 
               tempoCargoMinimo && pedagioCumprido;
    }
    
    /**
     * Verifica se o usuário é elegível para a regra de transição por pontos
     */
    public boolean verificarElegibilidadeRegraTransicaoPontos(Usuario usuario,
                                                            PeriodoTempo tempoContribuicao,
                                                            PeriodoTempo tempoServicoPublico,
                                                            PeriodoTempo tempoCargo,
                                                            LocalDate dataReferencia) {
        int idade = calcularIdade(usuario, dataReferencia);
        int pontos = calcularPontuacao(usuario, tempoContribuicao, dataReferencia);
        int anoReferencia = dataReferencia.getYear();
        
        boolean idadeMinima;
        boolean tempoContribuicaoMinimo;
        boolean pontosMinimos;
        
        if ("M".equals(usuario.sexo)) {
            idadeMinima = anoReferencia >= 2022 ? idade >= 62 : idade >= 61;
            tempoContribuicaoMinimo = tempoContribuicao.anos() >= 35;
            
            // Cálculo dos pontos mínimos com base no ano
            int pontosMinimosAno = Math.min(105, 96 + (anoReferencia - 2019));
            pontosMinimos = pontos >= pontosMinimosAno;
        } else {
            idadeMinima = anoReferencia >= 2022 ? idade >= 57 : idade >= 56;
            tempoContribuicaoMinimo = tempoContribuicao.anos() >= 30;
            
            // Cálculo dos pontos mínimos com base no ano
            int pontosMinimosAno = Math.min(100, 86 + (anoReferencia - 2019));
            pontosMinimos = pontos >= pontosMinimosAno;
        }
        
        boolean tempoServicoPublicoMinimo = tempoServicoPublico.anos() >= 20;
        boolean tempoCargoMinimo = tempoCargo.anos() >= 5;
        
        return idadeMinima && tempoContribuicaoMinimo && pontosMinimos && 
               tempoServicoPublicoMinimo && tempoCargoMinimo;
    }
    
    /**
     * Verifica elegibilidade para aposentadoria de professor
     */
    public boolean verificarElegibilidadeProfessor(Usuario usuario,
                                                 PeriodoTempo tempoMagisterio,
                                                 PeriodoTempo tempoServicoPublico,
                                                 PeriodoTempo tempoCargo,
                                                 LocalDate dataReferencia) {
        int idade = calcularIdade(usuario, dataReferencia);
        
        boolean idadeMinima = "M".equals(usuario.sexo) ? idade >= 60 : idade >= 57;
        boolean tempoContribuicaoMinimo = tempoMagisterio.anos() >= 25;
        boolean tempoServicoPublicoMinimo = tempoServicoPublico.anos() >= 10;
        boolean tempoCargoMinimo = tempoCargo.anos() >= 5;
        
        return idadeMinima && tempoContribuicaoMinimo && tempoServicoPublicoMinimo && tempoCargoMinimo;
    }
    
    /**
     * Verifica elegibilidade para aposentadoria por insalubridade
     */
    public boolean verificarElegibilidadeInsalubridade(Usuario usuario,
                                                    PeriodoTempo tempoInsalubre,
                                                    PeriodoTempo tempoServicoPublico,
                                                    PeriodoTempo tempoCargo,
                                                    LocalDate dataReferencia) {
        int idade = calcularIdade(usuario, dataReferencia);
        boolean idadeMinima = idade >= 60;
        boolean tempoContribuicaoMinimo = tempoInsalubre.anos() >= 25;
        boolean tempoServicoPublicoMinimo = tempoServicoPublico.anos() >= 10;
        boolean tempoCargoMinimo = tempoCargo.anos() >= 5;
        
        return idadeMinima && tempoContribuicaoMinimo && tempoServicoPublicoMinimo && tempoCargoMinimo;
    }
    
    /**
     * Executa uma simulação completa para um usuário
     */
    @Transactional
    public Simulacao executarSimulacao(Usuario usuario, List<PeriodoServico> periodosServico, 
                                      Simulacao.RegraAposentadoria regraAposentadoria,
                                      LocalDate dataReferencia) {
        Simulacao simulacao = new Simulacao();
        simulacao.usuario = usuario;
        simulacao.regraAposentadoria = regraAposentadoria;
        
        PeriodoTempo tempoContribuicao = calcularTempoContribuicao(periodosServico);
        PeriodoTempo tempoServicoPublico = calcularTempoServicoPublico(periodosServico);
        PeriodoTempo tempoCargo = calcularTempoCargo(usuario, periodosServico);
        
        simulacao.tempoContribuicaoAnos = tempoContribuicao.anos();
        simulacao.tempoContribuicaoMeses = tempoContribuicao.meses();
        simulacao.tempoContribuicaoDias = tempoContribuicao.dias();
        
        simulacao.tempoServicoPublicoAnos = tempoServicoPublico.anos();
        simulacao.tempoServicoPublicoMeses = tempoServicoPublico.meses();
        simulacao.tempoServicoPublicoDias = tempoServicoPublico.dias();
        
        simulacao.tempoCargoAnos = tempoCargo.anos();
        simulacao.tempoCargoMeses = tempoCargo.meses();
        simulacao.tempoCargoDias = tempoCargo.dias();
        
        simulacao.idadeAposentadoria = calcularIdade(usuario, dataReferencia);
        simulacao.pontuacao = calcularPontuacao(usuario, tempoContribuicao, dataReferencia);
        simulacao.percentualBeneficio = calcularPercentualBeneficio(tempoContribuicao);
        
        // Verificar elegibilidade com base na regra selecionada
        switch (regraAposentadoria) {
            case REGRA_PERMANENTE:
                simulacao.elegivel = verificarElegibilidadeRegraPermanente(
                    usuario, tempoContribuicao, tempoServicoPublico, tempoCargo, dataReferencia);
                break;
                
            case REGRA_TRANSICAO_PEDÁGIO:
                // Para este exemplo, consideramos pedágio como zero (já cumprido)
                simulacao.elegivel = verificarElegibilidadeRegraTransicaoPedagio(
                    usuario, tempoContribuicao, tempoServicoPublico, tempoCargo, dataReferencia, 
                    new PeriodoTempo(0, 0, 0));
                break;
                
            case REGRA_TRANSICAO_PONTOS:
                simulacao.elegivel = verificarElegibilidadeRegraTransicaoPontos(
                    usuario, tempoContribuicao, tempoServicoPublico, tempoCargo, dataReferencia);
                break;
                
            case REGRA_ESPECIAL_PROFESSOR:
                // Cálculo de tempo de magistério (simplificado para este exemplo)
                PeriodoTempo tempoMagisterio = periodosServico.stream()
                    .filter(p -> !p.concomitante && p.tipoServico == PeriodoServico.TipoServico.MAGISTERIO)
                    .map(PeriodoServico::calcularTempoComConversao)
                    .reduce(new PeriodoTempo(0, 0, 0), PeriodoTempo::somar);
                
                simulacao.elegivel = verificarElegibilidadeProfessor(
                    usuario, tempoMagisterio, tempoServicoPublico, tempoCargo, dataReferencia);
                break;
                
            case REGRA_ESPECIAL_INSALUBRIDADE:
                // Cálculo de tempo insalubre
                PeriodoTempo tempoInsalubre = periodosServico.stream()
                    .filter(p -> !p.concomitante && (p.tipoServico == PeriodoServico.TipoServico.INSALUBRE || p.insalubridade))
                    .map(PeriodoServico::calcularTempoComConversao)
                    .reduce(new PeriodoTempo(0, 0, 0), PeriodoTempo::somar);
                
                simulacao.elegivel = verificarElegibilidadeInsalubridade(
                    usuario, tempoInsalubre, tempoServicoPublico, tempoCargo, dataReferencia);
                break;
                
            default:
                simulacao.elegivel = false;
                simulacao.observacoes = "Regra de aposentadoria não implementada";
        }
        
        // Persiste a simulação
        simulacao.persist();
        
        return simulacao;
    }
}
