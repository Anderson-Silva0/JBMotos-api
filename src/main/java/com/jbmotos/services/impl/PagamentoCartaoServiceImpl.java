package com.jbmotos.services.impl;

import com.jbmotos.api.dto.PagamentoCartaoDTO;
import com.jbmotos.model.entity.PagamentoCartao;
import com.jbmotos.model.repositories.PagamentoCartaoRepository;
import com.jbmotos.services.PagamentoCartaoService;
import com.jbmotos.services.VendaService;
import com.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.jbmotos.services.exception.RegraDeNegocioException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
public class PagamentoCartaoServiceImpl implements PagamentoCartaoService {

    private final String PAGAMENTO_CARTAO_NAO_ENCONTRADO = "Pagamento em Cartão não encontrado para o Id informado.";

    @Autowired
    private PagamentoCartaoRepository repository;

    @Autowired
    private VendaService vendaService;

    @Autowired
    private ModelMapper mapper;


    @Transactional
    @Override
    public PagamentoCartao salvarPagamentoCartao(PagamentoCartaoDTO pagamentoCartaoDTO) {
        if (repository.existsByVendaId(pagamentoCartaoDTO.getIdVenda())) {
            throw new RegraDeNegocioException("Essa Venda já possui um Pagamento em Cartão de Crédito.");
        }

        PagamentoCartao pagamentoCartao = mapper.map(pagamentoCartaoDTO, PagamentoCartao.class);
        var venda = vendaService.buscarVendaPorId(pagamentoCartaoDTO.getIdVenda());
        pagamentoCartao.setVenda(venda);
        return repository.save(pagamentoCartao);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PagamentoCartao> buscarTodosPagamentosCartoes() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public PagamentoCartao buscarPagamentoCartaoPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(PAGAMENTO_CARTAO_NAO_ENCONTRADO));
    }

    @Transactional
    @Override
    public PagamentoCartao atualizarPagamentoCartao(PagamentoCartaoDTO pagamentoCartaoDTO) {
        var pagamentoCartao = buscarPagamentoCartaoPorId(pagamentoCartaoDTO.getId());

        validarVendaPagamentoCartao(pagamentoCartao, pagamentoCartaoDTO);

        pagamentoCartao.setParcela(pagamentoCartaoDTO.getParcela());
        pagamentoCartao.setBandeira(pagamentoCartaoDTO.getBandeira());
        pagamentoCartao.setTotalTaxas(pagamentoCartaoDTO.getTotalTaxas());

        return repository.save(pagamentoCartao);
    }

    @Transactional
    @Override
    public void deletarPagamentoCartao(Integer id) {
        existePagamentoCartao(id);
        repository.deleteById(id);
    }

    @Override
    public void existePagamentoCartao(Integer id) {
        if (!repository.existsById(id)) {
            throw new ObjetoNaoEncontradoException(PAGAMENTO_CARTAO_NAO_ENCONTRADO);
        }
    }

    private void validarVendaPagamentoCartao(PagamentoCartao pagamentoAntigo, PagamentoCartaoDTO pagamentoNovo) {
        if (!pagamentoAntigo.getVenda().getId().equals(pagamentoNovo.getIdVenda())) {
            throw new RegraDeNegocioException("Erro ao tentar atualizar. A Venda do Pagamento em Cartão" +
                    " não pode ser atualizada.");
        }
    }
}
