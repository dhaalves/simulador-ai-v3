package br.gov.aposentadoria.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulacoes")
public class Simulacao extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    public Usuario usuario;

    @Column(name = "data_simulacao", nullable = false)
    public LocalDateTime dataSimulacao = LocalDateTime.now();

    @Column(name = "nome_simulacao")
    public String nomeSimulacao;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "regra_aposentadoria", nullable = false)
    public RegraAposentadoria regraAposentadoria;
    
    @Column(name = "tempo_contribuicao_anos")
    public Integer tempoContribuicaoAnos;
    
    @Column(name = "tempo_contribuicao_meses")
    public Integer tempoContribuicaoMeses;
    
    @Column(name = "tempo_contribuicao_dias")
    public Integer tempoContribuicaoDias;
    
    @Column(name = "tempo_servico_publico_anos")
    public Integer tempoServicoPublicoAnos;
    
    @Column(name = "tempo_servico_publico_meses")
    public Integer tempoServicoPublicoMeses;
    
    @Column(name = "tempo_servico_publico_dias")
    public Integer tempoServicoPublicoDias;
    
    @Column(name = "tempo_cargo_anos")
    public Integer tempoCargoAnos;
    
    @Column(name = "tempo_cargo_meses")
    public Integer tempoCargoMeses;
    
    @Column(name = "tempo_cargo_dias")
    public Integer tempoCargoDias;
    
    @Column(name = "data_previsao_aposentadoria")
    public LocalDate dataPrevisaoAposentadoria;
    
    @Column(name = "idade_aposentadoria")
    public Integer idadeAposentadoria;
    
    @Column(name = "pontuacao")
    public Integer pontuacao;
    
    @Column(name = "percentual_beneficio")
    public Double percentualBeneficio;
    
    @Column(name = "elegivel")
    public Boolean elegivel;
    
    @Column(name = "valor_beneficio_estimado")
    public Double valorBeneficioEstimado;
    
    @Column(name = "observacoes", length = 1000)
    public String observacoes;

    public enum RegraAposentadoria {
        REGRA_PERMANENTE("Regra Permanente"), 
        REGRA_TRANSICAO_PEDÁGIO("Regra de Transição - Pedágio"), 
        REGRA_TRANSICAO_PONTOS("Regra de Transição - Pontos"),
        REGRA_ESPECIAL_PROFESSOR("Regra Especial - Professor"),
        REGRA_ESPECIAL_POLICIAL("Regra Especial - Policial"),
        REGRA_ESPECIAL_INSALUBRIDADE("Regra Especial - Insalubridade");
        
        private final String descricao;
        
        RegraAposentadoria(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
    }
}
