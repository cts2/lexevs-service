/*
* Copyright: (c) 2004-2013 Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Except as contained in the copyright notice above, or as used to identify
* MFMER as the author of this software, the trade names, trademarks, service
* marks, or product names of the copyright holder shall not be used in
* advertising, promotion or otherwise in connection with this software without
* prior written authorization of the copyright holder.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PropertyReference;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

public class LexEvsEntityQueryServiceTestIT extends AbstractTestITBase {
	
	@Resource
	private LexEvsEntityQueryService service;
	
	@Resource
	private LexBIGService lbs;

	// local methods
	// --------------
	private EntityDescriptionQuery createQuery(String matchAlgorithmReference, String matchValue, String codeSystemVersion){
		// Create filters for query
		// ------------------------
		Set<ResolvedFilter> filters = new HashSet<ResolvedFilter>();	
		ResolvedFilter filter = this.createFilter(matchAlgorithmReference,  matchValue, null);				
		filters.add(filter);
		
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.setCodeSystemVersion(ModelUtils.nameOrUriFromName(codeSystemVersion));
		
		
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, filters, restrictions);
		return query;
	}
	
	private ResolvedFilter createFilter(String matchAlgorithmReference, String matchValue, PropertyReference propertyReference){
		ResolvedFilter filter = new ResolvedFilter();			
		filter.setMatchAlgorithmReference(new MatchAlgorithmReference(matchAlgorithmReference));
		filter.setMatchValue(matchValue);
		filter.setPropertyReference(propertyReference);				// Should this field be used??			
		return filter;
	}


	
	
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
		assertNotNull(this.lbs);
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_CodingSchemeExists_andNotEmpty() throws Exception {
		final NameOrURI name = ModelUtils.nameOrUriFromName("Automobiles");
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
		
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.setCodeSystemVersion(ModelUtils.nameOrUriFromName(name.getName()));
		
		// Create query, no filters
		// -------------------------
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, null, restrictions);	
		
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();		
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();
		assertNotNull(list);
		assertTrue(list.size() > 0);		
	}

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_CodingSchemeDoesNotExist() throws Exception {
		final NameOrURI name = ModelUtils.nameOrUriFromName("Automooobiles");
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
		
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.setCodeSystemVersion(ModelUtils.nameOrUriFromName(name.getName()));
		
		// Create query, no filters
		// -------------------------
		EntityDescriptionQuery query = new EntityDescriptionQueryImpl(null, null, restrictions);	
		
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();		
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		
		// Test results
		// ------------
		assertNull(directoryResult);		
	}
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_startsWith() throws Exception {
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
				
		// Create query
		// ------------
		EntityDescriptionQuery query = this.createQuery("startsWith", "Jaguar", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);

		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() > 0);
	}	

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_startsWith_Empty() throws Exception {
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
				
		// Create query
		// ------------
		EntityDescriptionQuery query = this.createQuery("startsWith", "JUGUAR", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);

		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() == 0);
	}	

	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_exactMatch() throws Exception {
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
				
		// Create query
		// ------------
		EntityDescriptionQuery query = this.createQuery("exactMatch", "Jaguar", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);

		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() > 0);
	}	

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_exactMatch_Empty() throws Exception {
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
				
		// Create query
		// ------------
		EntityDescriptionQuery query = this.createQuery("exactMatch", "Jagua", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);

		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() == 0);
	}	

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_contains() throws Exception {
		
		// NOTE:  The CTS2 "word starts with" filtered query maps to the LexEVS "contains" registered
		//   filter extension
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
		
		// Create query
		// ------------
		EntityDescriptionQuery query = this.createQuery("contains", "GE", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() > 0);
	}	
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_contains_Empty() throws Exception {
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
		
		// Create query
		// ------------
		EntityDescriptionQuery query = this.createQuery("contains", "MOOVIES", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() == 0);
	}	

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_contains_Multiple() throws Exception {
		
		// NOTE:  The CTS2 "word starts with" filtered query maps to the LexEVS "contains" registered
		//   filter extension
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
		
		// Create query
		// ------------
		EntityDescriptionQuery query = this.createQuery("contains", "Car", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() == 4);
	}	
	

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_Leading_Wildcard() throws Exception {
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
		
		// Create query - searching for "General Motors" entity
		// ------------
		EntityDescriptionQuery query = this.createQuery("LuceneQuery", "*neRal motors", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() > 0);
	}	
	
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_Leading_Wildcard_Empty() throws Exception {
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
		
		// Create query
		// ------------
		EntityDescriptionQuery query = this.createQuery("LuceneQuery", "* eral", "Automobiles");
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() == 0);
	}	
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_Lagging_Wildcard() throws Exception {
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
		
		// Create query - searching for "General Motors" entity
		// ------------
		EntityDescriptionQuery query = this.createQuery("LuceneQuery", "general *", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() > 0);
	}	
	
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_Lagging_Wildcard_Empty() throws Exception {
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
		
		// Create query
		// ------------
		EntityDescriptionQuery query = this.createQuery("LuceneQuery", "eneral*", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() == 0);
	}	
	
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_LeadingAndLagging_Wildcard() throws Exception {
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
		
		// Create query - searching for "General Motors" entity
		// ------------
		EntityDescriptionQuery query = this.createQuery("LuceneQuery", "*eneRal *", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() > 0);
	}	
	
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_LeadingAndLagging_Wildcard_Empty() throws Exception {
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
		
		// Create query
		// ------------
		EntityDescriptionQuery query = this.createQuery("LuceneQuery", "* eneRal*", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
		
		
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() == 0);
	}	
	
	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_Paging() throws Exception {
		
		// NOTE:  The CTS2 "word starts with" filtered query maps to the LexEVS "contains" registered
		//   filter extension
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
		
		// Create query
		// ------------
		EntityDescriptionQuery query = this.createQuery("contains", "Car", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		page.setMaxToReturn(3);
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
				
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue("Expected first 1 of 2 pages returned ",list.size() == 3);
		assertFalse("Expected not to be at the end of the pages ", directoryResult.isAtEnd());
		
		page.setPage(1);
		directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
		list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue("Expected 1 page being the last page returned ", list.size() == 1);
		assertTrue("Expected to be at the end of the pages ", directoryResult.isAtEnd());

	}	

	@Test
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
	public void testGetResourceSummaries_Filter_Paging_Empty() throws Exception {
		
		// NOTE:  The CTS2 "word starts with" filtered query maps to the LexEVS "contains" registered
		//   filter extension
		
		// Configure service to use an anonymous transformer class
		// -------------------------------------------------------
		service.setEntityTransformer(new EntityTransform(){
			public EntityDirectoryEntry transform(ResolvedConceptReference reference){
				return new EntityDirectoryEntry();
			}
		});
		
		// Create query
		// ------------
		EntityDescriptionQuery query = this.createQuery("exactMatch", "waz up", "Automobiles");	
				
		// Call getResourceSummaries from service
		// --------------------------------------
		SortCriteria sortCriteria = null;
		Page page = new Page();
		DirectoryResult<EntityDirectoryEntry> directoryResult = this.service.getResourceSummaries(query, sortCriteria, page);
		assertNotNull(directoryResult);
				
		// Test results
		// ------------
		List<EntityDirectoryEntry> list = directoryResult.getEntries();		
		assertNotNull(list);
		assertTrue(list.size() == 0);
		assertTrue("Expected to be at the end of the pages ", directoryResult.isAtEnd());
	}	

}

