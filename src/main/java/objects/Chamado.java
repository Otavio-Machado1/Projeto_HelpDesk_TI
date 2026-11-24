package objects;

import javax.persistence.*;

// O Hibernate cria a tabela chamado (hbm2ddl.auto=update)
@Entity
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String titulo;
    @Column
    private String descricao;
    @Column
    private String prioridade;   
    @Column
    private String status;      
    // Lado proprietário: cria a coluna id_tecnico (FK)
    @ManyToOne
    @JoinColumn(name = "id_tecnico")
    private Tecnico tecnico;


    public Chamado() {
    }

    public Chamado(String titulo, String descricao, String prioridade, Tecnico tecnico) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.status = "Aberto";
        this.tecnico = tecnico;
    }


    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    @Override
    public String toString() {
        return "CHAMADO: " + id
             + "\nProblema: " + titulo
             + "\nDescrição: " + descricao
             + "\nPrioridade: " + prioridade
             + "\nStatus: " + status
             + "\nTécnico responsável: " + tecnico.getNome()
             + " (" + tecnico.getEspecialidade() + ")";
    }

}
