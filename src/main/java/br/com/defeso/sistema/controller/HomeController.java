package br.com.defeso.sistema.controller;

import br.com.defeso.sistema.dao.UsuarioRepository;
import br.com.defeso.sistema.dao.SimulacaoRepository;
import br.com.defeso.sistema.dao.ContatoRepository;
import br.com.defeso.sistema.dao.PesquisaRepository;
import br.com.defeso.sistema.domain.Usuario;
import br.com.defeso.sistema.domain.Simulacao;
import br.com.defeso.sistema.domain.Contato;
import br.com.defeso.sistema.domain.Pesquisa;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SimulacaoRepository simulacaoRepository;

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private PesquisaRepository pesquisaRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session) {
        if (session.getAttribute("usuario_id") == null) {
            return "redirect:/autenticacao";
        }
        return "perfil";
    }

    // O método painelAdmin foi removido daqui para evitar conflito com o AdminController

    // --- LÓGICA DO SIMULADOR DE DIREITOS ---
    @PostMapping("/direitos/simular")
    public String processarSimulacao(Simulacao sim, HttpSession session) {
        Long idLogado = (Long) session.getAttribute("usuario_id");
        if (idLogado == null) return "redirect:/autenticacao";

        String resultado = "APROVADO";
        if (sim.getTempoRegistro() < 1 ||
                "Sim".equals(sim.getOutraRenda()) ||
                "Sim".equals(sim.getOutroBeneficio())) {
            resultado = "REPROVADO";
        }

        sim.setResultado(resultado);

        usuarioRepository.findById(idLogado).ifPresent(user -> {
            sim.setUsuario(user);
            simulacaoRepository.save(sim);
        });

        return "redirect:/direitos?status=sucesso&resultado=" + resultado;
    }

    // --- LÓGICA DE CONTATO ---
    @PostMapping("/contato/enviar")
    public String enviarMensagem(Contato contato, HttpSession session) {
        Long idLogado = (Long) session.getAttribute("usuario_id");
        if (idLogado == null) return "redirect:/autenticacao";

        usuarioRepository.findById(idLogado).ifPresent(user -> {
            contato.setUsuario(user);
            contatoRepository.save(contato);
        });

        return "redirect:/contato?status=sucesso";
    }

    // --- LÓGICA DA PESQUISA SOCIOPROFISSIONAL (EDUCAÇÃO) ---
    @PostMapping("/educacao/pesquisa")
    public String salvarPesquisa(Pesquisa pesquisa, HttpSession session) {
        Long idLogado = (Long) session.getAttribute("usuario_id");
        if (idLogado == null) return "redirect:/autenticacao";

        usuarioRepository.findById(idLogado).ifPresent(user -> {
            pesquisa.setUsuario(user);
            pesquisaRepository.save(pesquisa);
        });

        return "redirect:/educacao?status=sucesso";
    }

    // Rotas de páginas estáticas
    @GetMapping("/regras")
    public String regras() { return "regras"; }

    @GetMapping("/direitos")
    public String direitos() { return "direitos"; }

    @GetMapping("/noticias")
    public String noticias() { return "noticias"; }

    @GetMapping("/educacao")
    public String educacao() { return "educacao"; }

    @GetMapping("/contato")
    public String contato() { return "contato"; }
}