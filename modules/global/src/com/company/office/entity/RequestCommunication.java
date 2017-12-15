package com.company.office.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;

@Table(name = "OFFICE_REQUEST_COMMUNICATION")
@Entity(name = "office$RequestCommunication")
public class RequestCommunication extends StandardEntity {
    private static final long serialVersionUID = -6870391072272525279L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID")
    protected Request request;

    @Column(name = "QUESTION")
    protected String question;

    @Column(name = "ANSWER")
    protected String answer;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    public void setRequest(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }


    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }


}