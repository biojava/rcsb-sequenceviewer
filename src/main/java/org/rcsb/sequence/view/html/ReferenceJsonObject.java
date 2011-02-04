package org.rcsb.sequence.view.html;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.model.PubMed;
import org.rcsb.sequence.model.Reference;

public class ReferenceJsonObject extends JSONObject 
{
	private static final Map<Long, ReferenceJsonObject> theMap;
	static
	{
		Map<Long, ReferenceJsonObject> foo = new HashMap<Long, ReferenceJsonObject>();

		for(AnnotationName an : AnnotationRegistry.getAllAnnotations())
		{
					
			List<Reference> references = an.getReferences();
			for ( Reference r: references){
				
				if(r != null && r.getPmid() != null && r.getPmid() > -1L && r.getPubmed() != null ) 
				{
					
					foo.put(r.getPmid(), new ReferenceJsonObject(r));
				}
			}
		}

		theMap = Collections.unmodifiableMap(foo);
	}

	public static JSONArray get(AnnotationName an)
	{
		List<Reference> references = an.getReferences();
		
		JSONArray referencesJSON = new JSONArray();
		for (Reference r : references){
			JSONObject data = theMap.get(r.getPmid());
			if ( data != null)
				referencesJSON.put(data);
		}
		
		return referencesJSON;
	}

	private ReferenceJsonObject(Reference r)
	{
		PubMed p = r.getPubmed();
		try
		{
			put("journal", p.getJournalTitle());
			put("issue", p.getIssueNumber());
			put("pages", p.getMedlinePages());
			put("authors", p.getAuthorList());
			put("title", p.getArticleTitle());
			put("pubYear", p.getPublishedYear());
			put("pubMonth", p.getPublishedMonth());
			put("pubDay", p.getPublishedDay());
			put("volume", p.getVolume());
			put("pmid", p.getPubmedId());
		}
		catch(Exception e)
		{
			System.err.println("Couldn't make JSONObject for reference with pmid " + r.getPmid() + " " +  e.getMessage());
		}
	}
}
