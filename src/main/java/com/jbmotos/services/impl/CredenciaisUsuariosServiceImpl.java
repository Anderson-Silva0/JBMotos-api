package com.jbmotos.services.impl;

import com.jbmotos.api.dto.AuthenticationDTO;
import com.jbmotos.api.dto.CredenciaisUsuariosDTO;
import com.jbmotos.model.entity.CredenciaisUsuarios;
import com.jbmotos.model.entity.Funcionario;
import com.jbmotos.model.enums.Situacao;
import com.jbmotos.model.repositories.CredenciaisUsuariosRepository;
import com.jbmotos.services.CredenciaisUsuariosService;
import com.jbmotos.services.FuncionarioService;
import com.jbmotos.services.exception.AutenticacaoException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CredenciaisUsuariosServiceImpl implements CredenciaisUsuariosService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CredenciaisUsuariosRepository repository;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private ModelMapper mapper;

    @Override
    public Authentication login(AuthenticationDTO dto) {
        try {
            CredenciaisUsuarios credenciais = repository.findCredenciaisByLogin(dto.login());

            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.login(), dto.senha());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            Funcionario funcionario = credenciais.getFuncionario();
            if (funcionario.getStatusFuncionario() == Situacao.ATIVO) {
                return auth;
            } else {
                throw new AutenticacaoException("Não é possível fazer login. Usuário inativo.");
            }
        } catch (AuthenticationException e) {
            throw new AutenticacaoException("Login ou senha inválidos");
        }
    }

    @Override
    @Transactional
    public void cadastrarUsuario(CredenciaisUsuariosDTO dto) {
        if (this.repository.findByLogin(dto.getLogin()) != null) {
            throw new AutenticacaoException("O login já existe");
        }

        Funcionario funcionarioSalvo = this.funcionarioService.salvarFuncionario(dto.getFuncionario());

        CredenciaisUsuarios usuarioMap = mapper.map(dto, CredenciaisUsuarios.class);
        String senhaCriptografada = new BCryptPasswordEncoder().encode(usuarioMap.getSenha());
        usuarioMap.setSenha(senhaCriptografada);
        usuarioMap.setFuncionario(funcionarioSalvo);

        repository.save(usuarioMap);
    }

}
