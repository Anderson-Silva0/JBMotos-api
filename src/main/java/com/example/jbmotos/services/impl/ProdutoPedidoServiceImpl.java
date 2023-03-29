package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.EstoqueDTO;
import com.example.jbmotos.api.dto.ProdutoPedidoDTO;
import com.example.jbmotos.model.entity.Estoque;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.model.entity.Produto;
import com.example.jbmotos.model.entity.ProdutoPedido;
import com.example.jbmotos.model.repositories.ProdutoPedidoRepository;
import com.example.jbmotos.services.EstoqueService;
import com.example.jbmotos.services.PedidoService;
import com.example.jbmotos.services.ProdutoPedidoService;
import com.example.jbmotos.services.ProdutoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoPedidoServiceImpl implements ProdutoPedidoService {

    private final String MSG_ERRO_SALVAR_PRODUTO_PEDIDO = "Não é possível realizar o Pedido pois a " +
            "quantidade solicitada do produto é maior do que a quantidade disponível em estoque.";

    private final String MSG_ERRO_ATUALIZAR_PRODUTO_PEDIDO = "Não é possível Atualizar o Pedido pois a " +
            "quantidade solicitada do Produto é maior do que a quantidade disponível em estoque.";

    private final String MSG_ERRO_ATUALIZAR_NOVO_PRODUTO = "Não é possível Atualizar o Pedido pois a quantidade " +
            "solicitada do novo Produto é maior do que a quantidade disponível em estoque.";

    @Autowired
    @Lazy
    private PedidoService pedidoService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private EstoqueService estoqueService;

    @Autowired
    private ProdutoPedidoRepository produtoPedidoRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public ProdutoPedido salvarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO){
        ProdutoPedido produtoPedido = obterProdutoPedidoParaSalvar(produtoPedidoDTO);
        verificarSeProdutoJaExisteNoPedidoParaSalvar(produtoPedido);
        return produtoPedidoRepository.save(produtoPedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoPedido> buscarTodosProdutoPedido(){
        return produtoPedidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProdutoPedido> buscarProdutoPedidoPorId(Integer id){
        validarProdutoPedido(id);
        return produtoPedidoRepository.findById(id);
    }

    @Override
    @Transactional
    public ProdutoPedido atualizarProdutoPedido(ProdutoPedidoDTO produtoPedidoDTO){
        validarProdutoPedido(produtoPedidoDTO.getId());
        ProdutoPedido produtoPedido = obterProdutoPedidoParaAtualizar(produtoPedidoDTO);
        verificarSeProdutoJaExisteNoPedidoParaAtualizar(produtoPedido);
        Estoque estoque = produtoPedido.getProduto().getEstoque();
        estoqueService.atualizarEstoque(mapper.map(estoque, EstoqueDTO.class));
        return produtoPedidoRepository.save(produtoPedido);
    }

    @Override
    @Transactional
    public void deletarProdutoPedidoPorId(Integer id){
        validarProdutoPedido(id);
        atualizarQtdEstoqueParaDeletar(id);
        produtoPedidoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoPedido> buscarProdutoPedidoPorIdPedido(Integer idPedido){
        return produtoPedidoRepository.findProdutoPedidoByPedidoId(idPedido);
    }

    @Override
    @Transactional(readOnly = true)
    public void validarProdutoPedido(Integer id){
        if (!produtoPedidoRepository.existsById(id)) {
            throw new ObjetoNaoEncontradoException("Produto do Pedido não encontrado para o Id informado.");
        }
    }

    private ProdutoPedido obterProdutoPedidoParaSalvar(ProdutoPedidoDTO produtoPedidoDTO){
        ProdutoPedido produtoPedido = mapper.map(produtoPedidoDTO, ProdutoPedido.class);
        produtoPedido.setPedido(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido()).get());
        produtoPedido.setProduto(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto()).get());

        Estoque estoque = produtoPedido.getProduto().getEstoque();
        validarEstoqueSalvar(produtoPedido.getQuantidade(), estoque.getQuantidade());

        produtoPedido.setValorUnidade(produtoPedido.getProduto().getPrecoVenda());
        produtoPedido.setValorTotal(
                produtoPedido.getValorUnidade().multiply(
                        BigDecimal.valueOf(produtoPedido.getQuantidade())
                )
        );
        atualizarQtdEstoqueParaSalvar(produtoPedido);
        return produtoPedido;
    }

    private ProdutoPedido obterProdutoPedidoParaAtualizar(ProdutoPedidoDTO produtoPedidoDTO){
        ProdutoPedido produtoPedido = buscarProdutoPedidoPorId(produtoPedidoDTO.getId()).get();

        if(produtoPedidoDTO.getIdProduto() != produtoPedido.getProduto().getId()) {
            atualizarPedidoComNovoProduto(produtoPedido, produtoPedidoDTO);
        } else {
            produtoPedido.setPedido(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido()).get());
            produtoPedido.setProduto(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto()).get());

            Estoque estoque = produtoPedido.getProduto().getEstoque();
            validarEstoqueAtualizar(
                    estoque.getQuantidade(),
                    produtoPedidoDTO.getQuantidade(),
                    produtoPedido.getQuantidade()
            );
            atualizarQtdEstoqueParaAtualizar(produtoPedido, produtoPedidoDTO);
        }

        produtoPedido.setValorUnidade(produtoPedido.getProduto().getPrecoVenda());
        produtoPedido.setValorTotal(
                produtoPedido.getValorUnidade().multiply(
                        BigDecimal.valueOf(produtoPedidoDTO.getQuantidade())
                )
        );
        return produtoPedido;
    }

    private void atualizarPedidoComNovoProduto(ProdutoPedido produtoPedido, ProdutoPedidoDTO produtoPedidoDTO) {
        Integer qtdProdutoPedidoAntigo = produtoPedido.getQuantidade();
        Integer qtdEstoqueAntigo = produtoPedido.getProduto().getEstoque().getQuantidade();

        //Devolver a quantidade de estoque do produto antigo, pois mudou de produto.
        produtoPedido.getProduto().getEstoque().setQuantidade(qtdProdutoPedidoAntigo + qtdEstoqueAntigo);

        //Abater a quantidade de estoque do novo produto.
        Estoque estoque = produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto()).get().getEstoque();
        if (produtoPedidoDTO.getQuantidade() > estoque.getQuantidade()) {
            throw new RegraDeNegocioException(MSG_ERRO_ATUALIZAR_NOVO_PRODUTO);
        }
        estoque.setQuantidade(estoque.getQuantidade() - produtoPedidoDTO.getQuantidade());
        produtoPedido.setPedido(pedidoService.buscarPedidoPorId(produtoPedidoDTO.getIdPedido()).get());
        produtoPedido.setProduto(produtoService.buscarProdutoPorId(produtoPedidoDTO.getIdProduto()).get());
        produtoPedido.setQuantidade(produtoPedidoDTO.getQuantidade());
    }

    private void atualizarQtdEstoqueParaSalvar(ProdutoPedido produtoPedido){
        Estoque estoque = produtoPedido.getProduto().getEstoque();
        estoque.setQuantidade(estoque.getQuantidade() - produtoPedido.getQuantidade());
        estoqueService.atualizarEstoque(mapper.map(estoque, EstoqueDTO.class));
    }

    private void atualizarQtdEstoqueParaAtualizar(ProdutoPedido produtoPedido, ProdutoPedidoDTO produtoPedidoDTO) {
        Estoque estoque = produtoPedido.getProduto().getEstoque();

        Integer qtdAtualEstoque = estoque.getQuantidade();
        Integer qtdAnteriorProduto = produtoPedido.getQuantidade();
        Integer qtdNovaProduto = produtoPedidoDTO.getQuantidade();

        Integer novaQtdEstoque = qtdAtualEstoque + qtdAnteriorProduto - qtdNovaProduto;
        estoque.setQuantidade(novaQtdEstoque);

        produtoPedido.setQuantidade(qtdNovaProduto);
    }

    private void atualizarQtdEstoqueParaDeletar(Integer id) {
        ProdutoPedido produtoPedido = buscarProdutoPedidoPorId(id).get();
        estoqueService.adicionarQuantidadeAoEstoque(produtoPedido.getProduto().getId(), produtoPedido.getQuantidade());
    }

    private void validarEstoqueSalvar(Integer qtdProduto, Integer qtdEstoque){
        if (qtdProduto > qtdEstoque) {
            throw new RegraDeNegocioException(MSG_ERRO_SALVAR_PRODUTO_PEDIDO);
        }
    }

    private void validarEstoqueAtualizar(Integer qtdAtualEstoque,
                                         Integer qtdNovaProduto,
                                         Integer qtdAnteriorProduto){
        if ( qtdAtualEstoque + qtdAnteriorProduto - qtdNovaProduto < 0 ) {
            throw new RegraDeNegocioException(MSG_ERRO_ATUALIZAR_PRODUTO_PEDIDO);
        }
    }

    private void verificarSeProdutoJaExisteNoPedidoParaSalvar(ProdutoPedido produtoPedido){
        Pedido pedido = produtoPedido.getPedido();
        Produto produto = produtoPedido.getProduto();
        if (produtoPedidoRepository.existsProdutoPedidosByPedidoIdAndProdutoId(pedido.getId(),produto.getId())) {
            throw new RegraDeNegocioException("Erro ao tentar Salvar, Produto já adicionado ao Pedido.");
        }
    }

    private void verificarSeProdutoJaExisteNoPedidoParaAtualizar(ProdutoPedido produtoPedido){
        filtrarProdutoPedidoPorIdDiferente(produtoPedido).stream().forEach(produtoPedidoFiltrado -> {
            if (produtoPedido.getProduto().getId() == produtoPedidoFiltrado.getProduto().getId() &&
                    produtoPedido.getPedido() == produtoPedidoFiltrado.getPedido()) {
                throw new RegraDeNegocioException("Erro ao tentar Atualizar, Produto já adicionado ao Pedido.");
            }
        });
    }

    private List<ProdutoPedido> filtrarProdutoPedidoPorIdDiferente(ProdutoPedido produtoPedido){
        return buscarTodosProdutoPedido().stream()
                .filter(produtoPedidoFiltrado -> (produtoPedido.getId() != produtoPedidoFiltrado.getId()))
                .collect(Collectors.toList());
    }
}
