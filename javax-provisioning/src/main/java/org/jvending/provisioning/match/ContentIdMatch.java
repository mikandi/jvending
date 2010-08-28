package org.jvending.provisioning.match;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.provisioning.BundleDescriptor;
import javax.provisioning.MatchPolicy;

public class ContentIdMatch implements MatchPolicy {
    //collection of strings denoting ids
    private Collection<String> id;

    public ContentIdMatch(List<String> id) {
        this.id = id;
    }
    
    public ContentIdMatch(Collection<String> id) {
        this.id = id;
    }
    
    public ContentIdMatch(String contentId) {
    	this.id = Arrays.asList(contentId);
    }

    public float doMatch(BundleDescriptor bd) {
        if (bd == null) return 0f;
        if (id == null) return 1.0f;
        return id.contains(bd.getContentID()) ? 1.0f : 0f;
    }

}
