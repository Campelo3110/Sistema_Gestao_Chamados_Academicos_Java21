package br.edu.ifsp.lgp2.service;

import br.edu.ifsp.lgp2.dto.ChamadoAtualizacaoStatusDTO;
import br.edu.ifsp.lgp2.dto.ChamadoCadastroDTO;
import br.edu.ifsp.lgp2.dto.ChamadoEdicaoDTO;
import br.edu.ifsp.lgp2.factory.ChamadoFactory;
import br.edu.ifsp.lgp2.model.Chamado;
import br.edu.ifsp.lgp2.repository.ChamadoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/*
 * Service centraliza as operações e regras do sistema.
 */
@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;

    public ChamadoService(ChamadoRepository chamadoRepository) {
        this.chamadoRepository = chamadoRepository;
    }

    public List<Chamado> listarTodos() {
        return chamadoRepository.findAll();
    }

    public List<Chamado> filtrarPorStatus(String status) {
        if (status == null || status.isBlank()) {
            return listarTodos();
        }

        return chamadoRepository.findByStatus(status);
    }

    public Chamado buscarPorId(Long id) {
        return chamadoRepository.findById(id).orElse(null);
    }

    /** Optional explicita ao Controller REST a possibilidade de não encontrar o recurso. */
    public java.util.Optional<Chamado> buscarOpcional(Long id) {
        return chamadoRepository.findById(id);
    }

    public boolean existe(Long id) {
        return chamadoRepository.existsById(id);
    }

    public long quantidadeChamados() {
        return chamadoRepository.count();
    }

    public boolean dadosCadastroValidos(ChamadoCadastroDTO dto) {
        return dadosBasicosValidos(dto.getTitulo(), dto.getDescricao(), dto.getCategoria());
    }

    public boolean dadosEdicaoValidos(ChamadoEdicaoDTO dto) {
        return dadosBasicosValidos(dto.titulo(), dto.descricao(), dto.categoria())
                && (dto.status() == null || statusValido(dto.status()))
                && (dto.prioridade() == null || List.of("BAIXA", "MEDIA", "ALTA", "URGENTE").contains(dto.prioridade()))
                && dentroDoLimite(dto.responsavel(), 100)
                && dentroDoLimite(dto.observacaoAtendimento(), 500);
    }

    public boolean dadosStatusValidos(ChamadoAtualizacaoStatusDTO dto) {
        return statusValido(dto.getStatus()) && dentroDoLimite(dto.getObservacaoAtendimento(), 500);
    }

    private boolean dadosBasicosValidos(String titulo, String descricao, String categoria) {
        return textoObrigatorioValido(titulo, 120)
                && textoObrigatorioValido(descricao, 500)
                && textoObrigatorioValido(categoria, 80);
    }

    private boolean textoObrigatorioValido(String texto, int limite) {
        return texto != null && !texto.isBlank() && dentroDoLimite(texto, limite);
    }

    private boolean dentroDoLimite(String texto, int limite) {
        return texto == null || texto.length() <= limite;
    }

    private boolean statusValido(String status) {
        return status != null && List.of("ABERTO", "EM_ANALISE", "CONCLUIDO", "CANCELADO").contains(status);
    }

    /*
     * Cadastro usando DTO.
     * O DTO traz apenas os dados que vieram do formulário.
     */
    public Chamado salvarNovoChamado(ChamadoCadastroDTO dto) {

        Chamado chamado = new Chamado();

        chamado.setTitulo(dto.getTitulo().strip());
        chamado.setDescricao(dto.getDescricao().strip());
        chamado.setCategoria(dto.getCategoria().strip());

        chamado = ChamadoFactory.criarChamado(chamado);

        return chamadoRepository.save(chamado);
    }

    /*
     * Edição geral do chamado.
     */
    public Chamado atualizarChamado(Long id, ChamadoEdicaoDTO dto) {
        // Recupera a entidade persistida, preservando a data de abertura original.
        Chamado salvo = chamadoRepository.findById(id).orElse(null);
        if (salvo == null) return null;
        salvo.setTitulo(dto.titulo().strip());
        salvo.setDescricao(dto.descricao().strip());
        salvo.setCategoria(dto.categoria().strip());
        if (dto.status() != null) salvo.setStatus(dto.status());
        if (dto.prioridade() != null) salvo.setPrioridade(dto.prioridade());
        if (dto.responsavel() != null) salvo.setResponsavel(dto.responsavel().strip());
        if (dto.observacaoAtendimento() != null) salvo.setObservacaoAtendimento(dto.observacaoAtendimento());
        salvo.setDataAtualizacao(LocalDate.now());
        return chamadoRepository.save(salvo);
    }

    /*
     * GABARITO DA ATIVIDADE:
     *
     * Atualiza apenas status e observação de atendimento.
     * O DTO protege os demais campos da Entity.
     */
    public Chamado atualizarStatus(ChamadoAtualizacaoStatusDTO dto) {

        Chamado chamado = chamadoRepository.findById(dto.getId()).orElse(null);

        if (chamado == null) {
            return null;
        }

        chamado.setStatus(dto.getStatus());
        chamado.setObservacaoAtendimento(dto.getObservacaoAtendimento());
        chamado.setDataAtualizacao(LocalDate.now());

        return chamadoRepository.save(chamado);
    }

    public void excluir(Long id) {
        chamadoRepository.deleteById(id);
    }
}
