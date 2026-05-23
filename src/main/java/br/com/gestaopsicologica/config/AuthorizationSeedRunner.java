package br.com.gestaopsicologica.config;

import br.com.gestaopsicologica.domain.Papel;
import br.com.gestaopsicologica.domain.Permissao;
import br.com.gestaopsicologica.repository.PapelRepository;
import br.com.gestaopsicologica.repository.PermissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Order(0)
@RequiredArgsConstructor
public class AuthorizationSeedRunner implements ApplicationRunner {
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_PROFISSIONAL = "PROFISSIONAL";

    private static final List<String> ADMIN_PERMISSIONS = List.of(
            "USUARIO_CADASTRAR",
            "AGENDAMENTO_VISUALIZAR_TODOS",
            "PACIENTE_VISUALIZAR",
            "PACIENTE_CRIAR",
            "PACIENTE_EDITAR",
            "PACIENTE_REMOVER",
            "AGENDAMENTO_VISUALIZAR",
            "AGENDAMENTO_CRIAR",
            "AGENDAMENTO_EDITAR",
            "AGENDAMENTO_REMOVER",
            "AGENDAMENTO_ALTERAR_STATUS",
            "PAGAMENTO_ALTERAR",
            "TIPO_ATENDIMENTO_VISUALIZAR",
            "TIPO_ATENDIMENTO_CRIAR",
            "TIPO_ATENDIMENTO_EDITAR",
            "TIPO_ATENDIMENTO_REMOVER"
    );

    private static final List<String> PROFISSIONAL_PERMISSIONS = List.of(
            "PACIENTE_VISUALIZAR",
            "PACIENTE_CRIAR",
            "PACIENTE_EDITAR",
            "PACIENTE_REMOVER",
            "AGENDAMENTO_VISUALIZAR",
            "AGENDAMENTO_CRIAR",
            "AGENDAMENTO_EDITAR",
            "AGENDAMENTO_REMOVER",
            "AGENDAMENTO_ALTERAR_STATUS",
            "TIPO_ATENDIMENTO_VISUALIZAR"
    );

    private final PermissaoRepository permissaoRepository;
    private final PapelRepository papelRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, List<String>> rolePermissions = new LinkedHashMap<>();
        rolePermissions.put(ROLE_ADMIN, ADMIN_PERMISSIONS);
        rolePermissions.put(ROLE_PROFISSIONAL, PROFISSIONAL_PERMISSIONS);

        Set<String> allPermissionCodes = new LinkedHashSet<>();
        rolePermissions.values().forEach(allPermissionCodes::addAll);

        Map<String, Permissao> permissionsByCode = seedPermissions(allPermissionCodes);
        seedRoles(rolePermissions, permissionsByCode);
    }

    private Map<String, Permissao> seedPermissions(Set<String> permissionCodes) {
        Map<String, Permissao> permissionsByCode = new LinkedHashMap<>();

        for (String code : permissionCodes) {
            Permissao permission = permissaoRepository.findByCodigo(code)
                    .orElseGet(() -> {
                        Permissao newPermission = new Permissao();
                        newPermission.setCodigo(code);
                        return permissaoRepository.save(newPermission);
                    });

            permissionsByCode.put(code, permission);
        }

        return permissionsByCode;
    }

    private void seedRoles(Map<String, List<String>> rolePermissions, Map<String, Permissao> permissionsByCode) {
        for (Map.Entry<String, List<String>> entry : rolePermissions.entrySet()) {
            Papel role = papelRepository.findByNome(entry.getKey())
                    .orElseGet(() -> papelRepository.save(Papel.builder().nome(entry.getKey()).build()));

            role.setPermissoes(entry.getValue().stream()
                    .map(permissionsByCode::get)
                    .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new)));

            papelRepository.save(role);
        }
    }
}
