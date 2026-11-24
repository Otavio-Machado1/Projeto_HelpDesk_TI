package objects;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// O Hibernate cria a tabela tecnico
@Entity
public class Tecnico {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String nome;
    @Column
    private String especialidade;

    // Lado inverso: não cria coluna no banco
    @OneToMany(mappedBy = "tecnico")

    private List<Chamado> chamados = new ArrayList<>();

    
    public Tecnico() {
    }

    
    public Tecnico(String nome, String especialidade) {
        this.nome = nome;
        this.especialidade = especialidade;
    }

   

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public List<Chamado> getChamados() {
        return chamados;
    }

    public void addChamado(Chamado chamado) {
        chamados.add(chamado);
    }

    public void removeChamado(Chamado chamado) {
        chamados.remove(chamado);
    }


    @Override
    public String toString() {
        return "Tecnico [id=" + id + ", nome=" + nome + ", especialidade=" + especialidade + "]";
    }

}
