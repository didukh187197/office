package com.company.office.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import java.util.Date;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Creatable;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.entity.Updatable;
import java.util.UUID;

@Table(name = "OFFICE_REQUEST_STEP_COMMUNICATION")
@Entity(name = "office$RequestStepCommunication")
public class RequestStepCommunication extends BaseUuidEntity implements Creatable, Updatable {
    private static final long serialVersionUID = -6870391072272525279L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_STEP_ID")
    protected RequestStep requestStep;

    @Column(name = "REQUEST_ID")
    protected UUID requestId;

    @Column(name = "QUESTION")
    protected String question;

    @Composition
    @OnDeleteInverse(DeletePolicy.DENY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_FILE_ID")
    protected FileDescriptor questionFile;

    @Column(name = "ANSWER")
    protected String answer;

    @Composition
    @OnDeleteInverse(DeletePolicy.DENY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANSWER_FILE_ID")
    protected FileDescriptor answerFile;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public UUID getRequestId() {
        return requestId;
    }


    public void setRequestStep(RequestStep requestStep) {
        this.requestStep = requestStep;
    }

    public RequestStep getRequestStep() {
        return requestStep;
    }


    public void setQuestionFile(FileDescriptor questionFile) {
        this.questionFile = questionFile;
    }

    public FileDescriptor getQuestionFile() {
        return questionFile;
    }

    public void setAnswerFile(FileDescriptor answerFile) {
        this.answerFile = answerFile;
    }

    public FileDescriptor getAnswerFile() {
        return answerFile;
    }


    @Override
    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    @Override
    public Date getUpdateTs() {
        return updateTs;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }


    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedBy() {
        return createdBy;
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


}