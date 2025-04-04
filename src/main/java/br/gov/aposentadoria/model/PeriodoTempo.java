package br.gov.aposentadoria.model;

/**
 * Classe utilitária para representar períodos de tempo em anos, meses e dias
 */
public record PeriodoTempo(int anos, int meses, int dias) {

    /**
     * Soma dois períodos de tempo
     */
    public PeriodoTempo somar(PeriodoTempo outro) {
        int totalDias = this.dias + outro.dias;
        int totalMeses = this.meses + outro.meses;
        int totalAnos = this.anos + outro.anos;
        
        // Ajustar dias excedentes
        if (totalDias >= 30) {
            totalMeses += totalDias / 30;
            totalDias = totalDias % 30;
        }
        
        // Ajustar meses excedentes
        if (totalMeses >= 12) {
            totalAnos += totalMeses / 12;
            totalMeses = totalMeses % 12;
        }
        
        return new PeriodoTempo(totalAnos, totalMeses, totalDias);
    }
    
    /**
     * Converte o período para dias
     */
    public long toDias() {
        return anos * 365 + meses * 30 + dias;
    }
    
    @Override
    public String toString() {
        return anos + " anos, " + meses + " meses e " + dias + " dias";
    }
}
