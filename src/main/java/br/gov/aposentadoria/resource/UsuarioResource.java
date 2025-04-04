package br.gov.aposentadoria.resource;

import br.gov.aposentadoria.model.Usuario;
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

import java.util.List;

@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema")
public class UsuarioResource {

    @GET
    @Operation(summary = "Listar usuários", description = "Lista todos os usuários cadastrados")
    public Response listarUsuarios() {
        List<Usuario> usuarios = Usuario.listAll();
        return Response.ok(usuarios).build();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar usuário", description = "Busca um usuário pelo ID")
    public Response buscarUsuario(@PathParam("id") Long id) {
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuário não encontrado")
                    .build();
        }
        
        return Response.ok(usuario).build();
    }
    
    @POST
    @Transactional
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário")
    public Response criarUsuario(@Valid Usuario usuario) {
        // Verificar se CPF já existe
        if (Usuario.count("cpf", usuario.cpf) > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("CPF já cadastrado")
                    .build();
        }
        
        // Verificar se email já existe
        if (Usuario.count("email", usuario.email) > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Email já cadastrado")
                    .build();
        }
        
        usuario.persist();
        
        return Response.status(Response.Status.CREATED)
                .entity(usuario)
                .build();
    }
    
    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    public Response atualizarUsuario(@PathParam("id") Long id, @Valid Usuario novoUsuario) {
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuário não encontrado")
                    .build();
        }
        
        // Verificar se CPF já existe para outro usuário
        if (!usuario.cpf.equals(novoUsuario.cpf) && Usuario.count("cpf = ?1 and id != ?2", novoUsuario.cpf, id) > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("CPF já cadastrado para outro usuário")
                    .build();
        }
        
        // Verificar se email já existe para outro usuário
        if (!usuario.email.equals(novoUsuario.email) && Usuario.count("email = ?1 and id != ?2", novoUsuario.email, id) > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Email já cadastrado para outro usuário")
                    .build();
        }
        
        // Atualizar campos
        usuario.nome = novoUsuario.nome;
        usuario.cpf = novoUsuario.cpf;
        usuario.dataNascimento = novoUsuario.dataNascimento;
        usuario.email = novoUsuario.email;
        usuario.senha = novoUsuario.senha; // Em uma implementação real, aplicaríamos hash à senha
        usuario.dataIngressoServicoPublico = novoUsuario.dataIngressoServicoPublico;
        usuario.cargoAtual = novoUsuario.cargoAtual;
        usuario.sexo = novoUsuario.sexo;
        
        usuario.persist();
        
        return Response.ok(usuario).build();
    }
    
    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Remover usuário", description = "Remove um usuário existente")
    public Response removerUsuario(@PathParam("id") Long id) {
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuário não encontrado")
                    .build();
        }
        
        usuario.delete();
        
        return Response.noContent().build();
    }
}
