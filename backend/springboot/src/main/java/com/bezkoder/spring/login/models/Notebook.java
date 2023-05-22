package com.bezkoder.spring.login.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "notebooks")
public class Notebook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "user_notebook",
            joinColumns = @JoinColumn(name = "notebook_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private User user = new User();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "notebook_code",
            joinColumns = @JoinColumn(name = "notebook_id"),
            inverseJoinColumns = @JoinColumn(name = "code_id"))
    private Set<Code> codes = new HashSet<>();

    public Notebook(){}

    public Notebook(User user){this.user = user;}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Code> getCodes() {
        return codes;
    }

    public void setCodes(Set<Code> codes) {
        this.codes = codes;
    }

    public void addCode(Code code){
        this.codes.add(code);
    }
}
