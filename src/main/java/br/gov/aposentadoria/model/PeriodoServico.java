package br.gov.aposentadoria.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "periodos_servico")
public class PeriodoServico extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    public Usuario usuario;

    @NotNull(message = "Data de início é obrigatória")
    @Column(name = "data_inicio", nullable = false)
    public LocalDate dataInicio;

    @NotNull(message = "Data de fim é obrigatória")
    @Column(name = "data_fim", nullable = false)
    public LocalDate dataFim;

    @Column(name = "orgao_empregador", nullable = false)
    public String orgaoEmpregador;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servico", nullable = false)
    public TipoServico tipoServico;

    @Column(name = "cargo")
    public String cargo;

    @Column(name = "numero_portaria")
    public String numeroPortaria;
    
    @Column(name = "tempo_convertido")
    public Boolean tempoConvertido = false;
    
    @Column(name = "fator_conversao")
    public Double fatorConversao;
    
    @Column(name = "insalubridade")
    public Boolean insalubridade = false;
    
    @Column(name = "concomitante")
    public Boolean concomitante = false;

    /**
     * Calcula a duração do período em dias.
     */
    public long calcularDiasServico() {
        return java.time.temporal.ChronoUnit.DAYS.between(dataInicio, dataFim.plusDays(1));
    }
    
    /**
     * Calcula a duração em anos, meses e dias
     */
    public PeriodoTempo calcularTempo() {
        long totalDias = calcularDiasServico();
        
        int anos = (int) (totalDias / 365);
        int meses = (int) ((totalDias % 365) / 30);
        int dias = (int) ((totalDias % 365) % 30);
        
        return new PeriodoTempo(anos, meses, dias);
    }
    
    /**
     * Calcula a duração considerando fatores de conversão se aplicável
     */
    public PeriodoTempo calcularTempoComConversao() {
        if (tempoConvertido && fatorConversao != null) {
            long totalDias = (long) (calcularDiasServico() * fatorConversao);
            
            int anos = (int) (totalDias / 365);
            int meses = (int) ((totalDias % 365) / 30);
            int dias = (int) ((totalDias % 365) % 30);
            
            return new PeriodoTempo(anos, meses, dias);
        } else {
            return calcularTempo();
        }
    }

    public enum TipoServico {
        ESTATUTARIO,
        CLT,
        CRES,
        SERVICO_PUBLICO_FEDERAL,
        SERVICO_PUBLICO_ESTADUAL,
        SERVICO_PUBLICO_MUNICIPAL,
        SERVICO_MILITAR,
        INSALUBRE,
        MAGISTERIO
    }
}
