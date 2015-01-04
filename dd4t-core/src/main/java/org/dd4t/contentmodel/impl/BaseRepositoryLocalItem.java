package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.*;
import org.dd4t.core.util.DateUtils;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for all tridion items except for publications and organizational items
 *
 * @author bjornl
 */
public abstract class BaseRepositoryLocalItem extends BaseItem implements RepositoryLocalItem {

    @JsonProperty("RevisionDate")
    protected String revisionDateAsString;

    @JsonProperty("Publication")
    @JsonDeserialize(as = PublicationImpl.class)
    protected Publication publication;

    @JsonProperty("OwningPublication")
    @JsonDeserialize(as = PublicationImpl.class)
    protected Publication owningPublication;

    @JsonProperty("Folder")
    @JsonDeserialize(as = OrganizationalItemImpl.class)
    protected OrganizationalItem organizationalItem;

    @JsonProperty("LastPublishedDate")
    protected String lastPublishedDateAsString;

    @JsonProperty("Version")
    protected int version;

    @JsonProperty("MetadataFields") @JsonDeserialize(contentAs = BaseField.class)
    protected Map<String, Field> metadata;

    @JsonProperty("Categories") @JsonDeserialize(contentAs = CategoryImpl.class)
    protected List<Category> categories;


    /**
     * Get the organizational item
     */
    @Override
    public OrganizationalItem getOrganizationalItem() {
        return organizationalItem;
    }

    /**
     * Set the organizational item
     */
    public void setOrganizationalItem(OrganizationalItem organizationalItem) {
        this.organizationalItem = organizationalItem;
    }

    /**
     * Get the publication
     */
    @Override
    public Publication getOwningPublication() {
        return owningPublication;
    }

    /**
     * Set the publication
     *
     * @param publication
     */
    public void setOwningPublication(Publication publication) {
        this.owningPublication = publication;
    }

    /**
     * Get the publication
     */
    @Override
    public Publication getPublication() {
        return publication;
    }

    /**
     * Set the publication
     *
     * @param publication
     */
    @Override
    public void setPublication(Publication publication) {
        this.publication = publication;
    }


    public DateTime getRevisionDate() {
        if (revisionDateAsString == null || revisionDateAsString.isEmpty()) {
            return new DateTime();
        }
        return DateUtils.convertStringToDate(revisionDateAsString);
    }

    public void setRevisionDate(DateTime date) {
        this.revisionDateAsString = date.toString();
    }

    public DateTime getLastPublishedDate() {
        if (lastPublishedDateAsString == null || lastPublishedDateAsString.isEmpty()) {
            return new DateTime();
        }
        return DateUtils.convertStringToDate(lastPublishedDateAsString);
    }

    public void setLastPublishedDate(DateTime date) {
        this.lastPublishedDateAsString = DateUtils.convertDateToString(date);
    }

    public int getVersion() {

        return version;
    }

    public void setVersion(int version) {

        this.version = version;
    }

    public Map<String, Field> getMetadata() {
        if (metadata == null) {
            metadata = new HashMap<String, Field>();
        }
        return metadata;
    }

    public void setMetadata(Map<String, Field> metadata) {
        this.metadata = metadata;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}