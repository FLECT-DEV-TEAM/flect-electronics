package model;

import javax.persistence.Column;
import javax.persistence.Id;

@SalesforceEntity
public class Community extends SalesforceModel {

    @Id
    @Column
    public String id;
    
    @Column
    public String name;
    
}
