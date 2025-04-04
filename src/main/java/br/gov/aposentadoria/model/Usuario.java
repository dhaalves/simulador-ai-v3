package br.gov.aposentadoria.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario extends PanacheEntity {

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    public String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Column(nullable = false, unique = true, length = 11)
    public String cpf;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @Column(name = "data_nascimento", nullable = false)
    public LocalDate dataNascimento;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    public String senha;
    
    @Column(name = "data_ingresso_servico_publico")
    public LocalDate dataIngressoServicoPublico;
    
    @Column(name = "cargo_atual")
    public String cargoAtual;
    
    @Column(name = "sexo")
    public String sexo;
    
    @OneToMany(mappedBy = "usuario")
    public List<PeriodoServico> periodosServico;
    
    @OneToMany(mappedBy = "usuario")
    public List<Simulacao> simulacoes;
}
