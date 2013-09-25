package model;

import javax.persistence.Column;

@SalesforceEntity
public class Idea extends SalesforceModel {
    
    @Column
    public String id;
    
    @Column
    public String title;
    
    @Column
    public String body;

}
