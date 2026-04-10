package br.com.defeso.sistema.controller;

import br.com.defeso.sistema.dao.*;
import br.com.defeso.sistema.domain.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class RegistroController {

    @Autowired
    private SimulacaoRepository simulacaoRepository;
    @Autowired
    private ContatoRepository contatoRepository;
    @Autowired
    private PesquisaRepository pesquisaRepository;
    @Autowired
    private RegistroFaunaRepository registroFaunaRepository;

    @GetMapping("/meus_registros")
    public String listarRegistros(HttpSession session, Model model) {
        Long idLogado = (Long) session.getAttribute("usuario_id");
        if (idLogado == null) return "redirect:/autenticacao";

        model.addAttribute("listaSimulacoes", simulacaoRepository.findByUsuarioId(idLogado));
        model.addAttribute("listaContatos", contatoRepository.findByUsuarioId(idLogado));
        model.addAttribute("listaPesquisas", pesquisaRepository.findByUsuarioId(idLogado));
        model.addAttribute("listaFauna", registroFaunaRepository.findByUsuarioId(idLogado));

        return "meus-registros";
    }

    // --- SALVAR REGISTRO DE FAUNA ---
    @PostMapping("/noticias/registrar")
    public String registrarFauna(HttpSession session,
                                 @ModelAttribute RegistroFauna fauna,
                                 @RequestParam("file") MultipartFile file) throws IOException {

        Long idLogado = (Long) session.getAttribute("usuario_id");
        if (idLogado == null) return "redirect:/autenticacao";

        fauna.setUsuarioId(idLogado);

        if (!file.isEmpty()) {
            String nomeImagem = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            // Caminho ajustado para garantir que a imagem seja salva corretamente
            Path caminho = Paths.get("src/main/resources/static/img/fauna/" + nomeImagem);
            Files.createDirectories(caminho.getParent());
            Files.copy(file.getInputStream(), caminho);
            fauna.setFotoNome(nomeImagem);
        }

        registroFaunaRepository.save(fauna);
        return "redirect:/noticias?status=sucesso";
    }

    // --- EDIÇÃO ---

    @PostMapping("/registros/editar/sim")
    public String editarSim(Simulacao sim) {
        Optional<Simulacao> op = simulacaoRepository.findById(sim.getId());
        if(op.isPresent()){
            Simulacao original = op.get();
            original.setEspecie(sim.getEspecie());
            original.setTempoRegistro(sim.getTempoRegistro());
            original.setOutraRenda(sim.getOutraRenda());
            original.setOutroBeneficio(sim.getOutroBeneficio());

            String resultado = (sim.getTempoRegistro() >= 1 &&
                    "Não".equals(sim.getOutraRenda()) &&
                    "Não".equals(sim.getOutroBeneficio())) ? "APROVADO" : "REPROVADO";

            original.setResultado(resultado);
            simulacaoRepository.save(original);
        }
        return "redirect:/meus_registros?edit=sucesso";
    }

    @PostMapping("/registros/editar/contato")
    public String editarContato(@RequestParam Long id, @RequestParam String mensagem) {
        contatoRepository.findById(id).ifPresent(original -> {
            original.setMensagem(mensagem);
            contatoRepository.save(original);
        });
        return "redirect:/meus_registros?edit=sucesso";
    }

    @PostMapping("/registros/editar/pesquisa")
    public String editarPesquisa(Pesquisa pes) {
        pesquisaRepository.findById(pes.getId()).ifPresent(original -> {
            original.setTipoEmbarcacao(pes.getTipoEmbarcacao());
            original.setFrequenciaPesca(pes.getFrequenciaPesca());
            original.setUsoBeneficio(pes.getUsoBeneficio());
            original.setSatisfacaoProfissao(pes.getSatisfacaoProfissao());
            original.setMaterialUtilizado(pes.getMaterialUtilizado());
            pesquisaRepository.save(original);
        });
        return "redirect:/meus_registros?edit=sucesso";
    }

    @PostMapping("/registros/editar/fauna")
    public String editarFauna(RegistroFauna fauna) {
        registroFaunaRepository.findById(fauna.getId()).ifPresent(original -> {
            original.setEspecie(fauna.getEspecie());
            original.setLocalizacao(fauna.getLocalizacao());
            original.setObservacoes(fauna.getObservacoes());
            registroFaunaRepository.save(original);
        });
        return "redirect:/meus_registros?edit=sucesso";
    }

    // --- DELETAR ---

    @GetMapping("/registros/delete/sim/{id}")
    public String deleteSim(@PathVariable Long id) {
        simulacaoRepository.deleteById(id);
        return "redirect:/meus_registros?delete=sucesso";
    }

    @GetMapping("/registros/delete/contato/{id}")
    public String deleteContato(@PathVariable Long id) {
        contatoRepository.deleteById(id);
        return "redirect:/meus_registros?delete=sucesso";
    }

    @GetMapping("/registros/delete/pesquisa/{id}")
    public String deletePesquisa(@PathVariable Long id) {
        pesquisaRepository.deleteById(id);
        return "redirect:/meus_registros?delete=sucesso";
    }

    // ROTA 1: Deleção via Usuário Comum (Redireciona para Meus Registros)
    @GetMapping("/registros/delete/fauna/{id}")
    public String deleteFauna(@PathVariable Long id) {
        registroFaunaRepository.deleteById(id);
        return "redirect:/meus_registros?delete=sucesso";
    }

    // ROTA 2: Deleção via Admin (Redireciona de volta para o Painel na aba Fauna)
    @GetMapping("/registros/delete/fauna/admin/{id}")
    public String deleteFaunaAdmin(@PathVariable Long id) {
        registroFaunaRepository.deleteById(id);
        return "redirect:/admin/painel?aba=fauna";
    }
}