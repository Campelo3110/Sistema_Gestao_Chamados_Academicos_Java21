package br.edu.ifsp.lgp2.controller;

import br.edu.ifsp.lgp2.dto.ChamadoAtualizacaoStatusDTO;
import br.edu.ifsp.lgp2.dto.ChamadoCadastroDTO;
import br.edu.ifsp.lgp2.dto.ChamadoEdicaoDTO;
import br.edu.ifsp.lgp2.dto.FiltroChamadoDTO;
import br.edu.ifsp.lgp2.dto.IndicadoresDTO;
import br.edu.ifsp.lgp2.model.Chamado;
import br.edu.ifsp.lgp2.service.ChamadoService;
import br.edu.ifsp.lgp2.service.ConsultaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chamados")
public class ChamadoApiController {
    private final ChamadoService chamados;
    private final ConsultaService consultas;

    public ChamadoApiController(ChamadoService chamados, ConsultaService consultas) {
        this.chamados = chamados;
        this.consultas = consultas;
    }

    @GetMapping
    public List<Chamado> listar(@ModelAttribute FiltroChamadoDTO filtro) {
        return consultas.consultar(filtro);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chamado> buscar(@PathVariable Long id) {
        return ResponseEntity.of(chamados.buscarOpcional(id));
    }

    @GetMapping("/indicadores")
    public IndicadoresDTO indicadores() {
        return consultas.indicadores();
    }

    @PostMapping
    public ResponseEntity<Chamado> criar(@RequestBody ChamadoCadastroDTO dto) {
        if (!chamados.dadosCadastroValidos(dto)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(chamados.salvarNovoChamado(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chamado> atualizar(@PathVariable Long id, @RequestBody ChamadoEdicaoDTO dto) {
        if (!chamados.dadosEdicaoValidos(dto)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.of(Optional.ofNullable(chamados.atualizarChamado(id, dto)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Chamado> status(@PathVariable Long id, @RequestBody ChamadoAtualizacaoStatusDTO dto) {
        if (!chamados.dadosStatusValidos(dto)) {
            return ResponseEntity.badRequest().build();
        }
        dto.setId(id);
        return ResponseEntity.of(Optional.ofNullable(chamados.atualizarStatus(dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!chamados.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        chamados.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

