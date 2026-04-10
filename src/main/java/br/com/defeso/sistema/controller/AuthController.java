package br.com.defeso.sistema.controller;

import br.com.defeso.sistema.domain.Usuario;
import br.com.defeso.sistema.dao.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping("/autenticacao")
    public String abrirAutenticacao() {
        return "autenticacao";
    }

    @PostMapping("/cadastro")
    public String cadastrar(Usuario usuario, @RequestParam("foto") MultipartFile arquivo, Model model) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("erro", "E-mail já cadastrado!");
            return "autenticacao";
        }

        usuario.setSenha(encoder.encode(usuario.getSenha()));

        if (usuario.getEmail().equals("admin@ecosis.com")) {
            usuario.setNivelAcesso("admin");
        }

        salvarFoto(usuario, arquivo);

        usuarioRepository.save(usuario);
        model.addAttribute("sucesso", "Cadastro realizado com segurança! Faça login.");
        return "autenticacao";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String senha, HttpSession session, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent() && encoder.matches(senha, usuarioOpt.get().getSenha())) {
            Usuario user = usuarioOpt.get();
            session.setAttribute("usuario_id", user.getId());
            session.setAttribute("usuario_nome", user.getNome());
            session.setAttribute("usuario_email", user.getEmail());
            session.setAttribute("usuario_foto", user.getFotoPerfil());
            session.setAttribute("usuario_nivel", user.getNivelAcesso());
            return "redirect:/";
        }

        model.addAttribute("erro", "E-mail ou senha incorretos!");
        return "autenticacao";
    }

    @PostMapping("/perfil/atualizar")
    public String atualizarPerfil(Usuario usuarioEditado,
                                  @RequestParam("foto") MultipartFile arquivo,
                                  HttpSession session,
                                  Model model) {

        Long idLogado = (Long) session.getAttribute("usuario_id");
        if (idLogado == null) return "redirect:/autenticacao";

        Usuario usuarioBanco = usuarioRepository.findById(idLogado).get();

        usuarioBanco.setNome(usuarioEditado.getNome());
        usuarioBanco.setEmail(usuarioEditado.getEmail());

        if (!arquivo.isEmpty()) {
            salvarFoto(usuarioBanco, arquivo);
        }

        usuarioRepository.save(usuarioBanco);

        session.setAttribute("usuario_nome", usuarioBanco.getNome());
        session.setAttribute("usuario_email", usuarioBanco.getEmail());
        session.setAttribute("usuario_foto", usuarioBanco.getFotoPerfil());

        model.addAttribute("mensagem_status", "sucesso");
        model.addAttribute("user", usuarioBanco);
        return "perfil";
    }

    // AJUSTADO: Agora redireciona para a Home ("/")
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private void salvarFoto(Usuario usuario, MultipartFile arquivo) {
        if (!arquivo.isEmpty()) {
            try {
                String pastaDestino = "src/main/resources/static/img/";
                String nomeArquivo = UUID.randomUUID().toString() + "_" + arquivo.getOriginalFilename();
                Path caminho = Paths.get(pastaDestino + nomeArquivo);
                Files.write(caminho, arquivo.getBytes());
                usuario.setFotoPerfil(nomeArquivo);
            } catch (Exception e) {
                System.out.println("Erro ao salvar foto: " + e.getMessage());
            }
        } else if (usuario.getFotoPerfil() == null) {
            usuario.setFotoPerfil("default_avatar.png");
        }
    }
}