package com.bezkoder.spring.login.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "surveys")
public class Survey{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 1000)
    private String json;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "survey_code",
            joinColumns = @JoinColumn(name = "survey_id"),
            inverseJoinColumns = @JoinColumn(name = "code_id"))
    private Code code = new Code();

    public Survey(){}
    public Survey(String json){
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }
}
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//import java.util.List;
//
//public class Survey {
//
//    @JsonProperty("title")
//    private String title;
//
//    @JsonProperty("pages")
//    private List<Page> pages;
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public List<Page> getPages() {
//        return pages;
//    }
//
//    public void setPages(List<Page> pages) {
//        this.pages = pages;
//    }
//
//    public static class Page {
//
//        @JsonProperty("name")
//        private String name;
//
//        @JsonProperty("elements")
//        private List<Element> elements;
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public List<Element> getElements() {
//            return elements;
//        }
//
//        public void setElements(List<Element> elements) {
//            this.elements = elements;
//        }
//
//        public static class Element {
//
//            @JsonProperty("type")
//            private String type;
//
//            @JsonProperty("name")
//            private String name;
//
//            // Add additional properties for each Survey Creator element type as needed
//
//            public String getType() {
//                return type;
//            }
//
//            public void setType(String type) {
//                this.type = type;
//            }
//
//            public String getName() {
//                return name;
//            }
//
//            public void setName(String name) {
//                this.name = name;
//            }
//
//            // Add getters and setters for additional properties as needed
//        }
//    }
//}
//
