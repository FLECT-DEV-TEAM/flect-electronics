package model;

import helper.Salesforce;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@SalesforceEntity
public class QuestionDataCategorySelection extends SalesforceModel {

    @Id
    @Column
    public String id;
    
    @Column
    public String dataCategoryGroupName;
    
    @Column
    public String dataCategoryName;
    
    @Column
    public String parentId;
    
    @OneToOne
    public Question question;
    
    // FIXME 204SOLSRV-27
    public static List<QuestionDataCategorySelection> findByCategoryName(String categoryName) {
        return Salesforce
                    .find(QuestionDataCategorySelection.class)
                    .where("QuestionDataCategorySelection.dataCategoryName = ?", categoryName)
                    .getList();
    }
    
    public static QuestionDataCategorySelection findByQuestionId(String id) {
        return Salesforce
                .find(QuestionDataCategorySelection.class)
                .where("QuestionDataCategorySelection.parentId = ?", id)
                .getSingle();
    }
    
    public static void save(String questionId, String categoryName) {
        QuestionDataCategorySelection category
            = new QuestionDataCategorySelection();
        category.dataCategoryGroupName = "AnswerGroup";
        category.dataCategoryName = categoryName;
        category.parentId = questionId;
        Salesforce.save(category).portalUser().execute();
    }
    
}
