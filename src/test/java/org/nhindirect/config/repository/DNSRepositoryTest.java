package org.nhindirect.config.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.nhindirect.common.crypto.CryptoExtensions;
import org.nhindirect.config.SpringBaseTest;
import org.nhindirect.config.store.DNSRecord;
import org.nhindirect.config.store.util.DNSRecordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.xbill.DNS.Type;

public class DNSRepositoryTest extends SpringBaseTest
{
	
	private static final String certBasePath = "src/test/resources/certs/"; 
	
	@Autowired
	private DNSRepository dnsRepo;	
	
	static
	{
		CryptoExtensions.registerJCEProviders();
	}	
	
	private byte[] loadCertificateData(String certFileName) throws Exception
	{
		File fl = new File(certBasePath + certFileName);
		
		return FileUtils.readFileToByteArray(fl);
	}
	
	
	@Before
	public void cleanDataBase()
	{
		dnsRepo.deleteAll();
	}
	
	@Test
	public void testCleanDatabase() throws Exception 
	{
		final List<DNSRecord> records = dnsRepo.findAll();
		
		assertEquals(0, records.size());
	}

	@Test
	public void testAddCertRecord() throws Exception
	{

		byte[] certData = loadCertificateData("gm2552.der");
		assertNotNull(certData);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(certData);

    	X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
    	
    	DNSRecord record1 = DNSRecordUtils.createX509CERTRecord("gm2552@securehealthemail.com", 86400L, cert);
    	
    	dnsRepo.saveAll(Arrays.asList(record1));
    	List<DNSRecord> records = dnsRepo.findByType(Type.CERT);
    	
    	assertEquals(1, records.size());
    	assertEquals(record1, records.iterator().next());
    	

	}

	@Test
	public void testAddSingleARecords() throws Exception 
	{
		
		// Add 1 record
		DNSRecord record = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1"); 
		dnsRepo.saveAll(Arrays.asList(record));
		
		final Collection<DNSRecord> records = dnsRepo.findByNameIgnoreCase(record.getName());
		
		assertEquals(1, records.size());
		
		DNSRecord compareRec = records.iterator().next();
		assertEquals(record.getName(), compareRec.getName());
		assertEquals(Type.A, compareRec.getType());
	}	

	@Test
	public void testMultipleARecords() throws Exception 
	{
		
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1"); 
		DNSRecord record2 = DNSRecordUtils.createARecord("example2.domain.com", 86400L, "74.22.43.123"); 
		DNSRecord record3 = DNSRecordUtils.createARecord("sample.domain.com", 86400L, "81.142.48.20"); 
		
		dnsRepo.saveAll(Arrays.asList(record1, record2, record3));
		

		 //Get by name

		Collection<DNSRecord> records = dnsRepo.findByNameIgnoreCase(record1.getName().toUpperCase());
		
		assertEquals(1, records.size());
		
		DNSRecord compareRec = records.iterator().next();
		assertEquals(record1.getName(), compareRec.getName());
		assertEquals(Type.A, compareRec.getType());
		

		 //Get all types... this will actually become a find all 
		// call
		
		/**
		 *  Move up to biz logic
		 **/ 
		records = dnsRepo.findAll();
		assertEquals(3, records.size());
		
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));
		assertTrue(records.contains(record3));
		

		 //Get A only

		records = dnsRepo.findByType(Type.A);
		assertEquals(3, records.size());
		
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));
		assertTrue(records.contains(record3));
		
		
		 //Get SRV only
		 
		records = dnsRepo.findByType(Type.SRV);
		assertEquals(0, records.size());		
	}	
	
	
	@Test
	public void testAddRecord_invalidType() throws Exception 
	{
		/**
		 *  Move up to biz logic
		**/ 
		/*
		DNSRecord record = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1"); 
		record.setType(Type.ANY);
		
		boolean exceptionOccured = false;
		try
		{	
			dnsDao.add(Arrays.asList(record));
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
	
		assertTrue(exceptionOccured);
		*/
	}
	
	@Test
	public void testAddRecord_recordAlreadyExistsWithRdata() throws Exception 
	{
		
		/**
		 *  Move up to biz logic
		**/ 
		/*
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1"); 
		dnsRepo.saveAll(Arrays.asList(record1));
		
		DNSRecord record2 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		boolean exceptionOccured = false;
		try
		{	
			dnsRepo.saveAll(Arrays.asList(record2));
		}
		catch (DataIntegrityViolationException e)
		{
			exceptionOccured = true;
		}
	
		assertTrue(exceptionOccured);
		*/
	}	
	
	@Test
	public void testMultipleARecords_differentRdata() throws Exception 
	{
		
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1"); 

		
		dnsRepo.saveAll(Arrays.asList(record1));
		
		DNSRecord record2 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.2"); 
		dnsRepo.saveAll(Arrays.asList(record2));
		
		//Get by name
		 
		Collection<DNSRecord> records = dnsRepo.findByNameIgnoreCase(record1.getName());
		
		assertEquals(2, records.size());
		
		records.contains(record1);
		records.contains(record2);
			
	}	

	@Test
	public void testGetByType() throws Exception 
	{

		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1"); 
		DNSRecord record2 = DNSRecordUtils.createARecord("example2.domain.com", 86400L, "127.0.0.1"); 
		DNSRecord record3 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example.domain.com", 86400L, 3506, 1, 1); 
		dnsRepo.saveAll(Arrays.asList(record1, record2, record3));

		
		 // By A
		 
		Collection<DNSRecord> records = dnsRepo.findByType(Type.A);
		
		assertEquals(2, records.size());
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));

		
		
		 // By SRV
		 
		records = dnsRepo.findByType(Type.SRV);
		
		assertEquals(1, records.size());
		assertTrue(records.contains(record3));
		
		
		// By ANY
		/**
		 *  Move up to biz logic
		**/ 		
		records = dnsRepo.findAll();		
		
		assertEquals(3, records.size());
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));		
		assertTrue(records.contains(record3));		
	}		
	
	@Test
	public void testGetByName() throws Exception 
	{
		
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		DNSRecord record2 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.2"); 
		DNSRecord record3 = DNSRecordUtils.createARecord("example2.domain.com", 86400L, "127.0.0.3"); 
		DNSRecord record4 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example.domain.com", 86400L, 3506, 1, 1); 
		DNSRecord record5 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example2.domain.com", 86400L, 3506, 1, 1);
		dnsRepo.saveAll(Arrays.asList(record1, record2, record3, record4, record5));

		Collection<DNSRecord> records = dnsRepo.findByNameIgnoreCase(record1.getName().toUpperCase());
		
		assertEquals(2, records.size());
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));


		records = dnsRepo.findByNameIgnoreCase(record3.getName());
		
		assertEquals(1, records.size());
		assertTrue(records.contains(record3));
		

		records = dnsRepo.findByNameIgnoreCase(record4.getName());		
		
		assertEquals(2, records.size());
		assertTrue(records.contains(record4));
		assertTrue(records.contains(record5));		
		
		
		records = dnsRepo.findByNameIgnoreCase("bogus.com.");		
		
		assertEquals(0, records.size());
	}		
	
	@Test
	public void testGetByRecord() throws Exception 
	{
		
		testCleanDatabase();
		
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		DNSRecord record2 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.2"); 
		DNSRecord record3 = DNSRecordUtils.createARecord("example2.domain.com", 86400L, "127.0.0.3"); 
		DNSRecord record4 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example.domain.com", 86400L, 3506, 1, 1); 
		DNSRecord record5 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example2.domain.com", 86400L, 3506, 1, 1);
		dnsRepo.saveAll(Arrays.asList(record1, record2, record3, record4, record5));

		Collection<DNSRecord> records = dnsRepo.findByNameIgnoreCase(record3.getName());
		
		assertEquals(1, records.size());
		Optional<DNSRecord> checkRec = dnsRepo.findById(records.iterator().next().getId());
		assertNotNull(checkRec);
		assertEquals(checkRec.get(), record3);


		records = dnsRepo.findAll();
		assertEquals(5, records.size());
		List<Long> ids = new ArrayList<>(records.size());

		for (DNSRecord record : records)
			ids.add(record.getId());
		
		records = dnsRepo.findAllById(ids);
		assertEquals(5, records.size());
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));
		assertTrue(records.contains(record3));
		assertTrue(records.contains(record4));
		assertTrue(records.contains(record5));
	}	
	
	@Test
	public void testGetCount() throws Exception 
	{
		assertEquals(0, dnsRepo.count());
		
		// Add 5 record
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		DNSRecord record2 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.2"); 
		DNSRecord record3 = DNSRecordUtils.createARecord("example2.domain.com", 86400L, "127.0.0.3"); 
		DNSRecord record4 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example.domain.com", 86400L, 3506, 1, 1); 
		DNSRecord record5 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example2.domain.com", 86400L, 3506, 1, 1);
		dnsRepo.saveAll(Arrays.asList(record1, record2, record3, record4, record5));

		assertEquals(5, dnsRepo.count());
		
	}	
	
	
	@Test
	public void testRemoveByRecords() throws Exception 
	{
		
		assertEquals(0, dnsRepo.count());
		
		// Add 5 record
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		DNSRecord record2 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.2"); 
		DNSRecord record3 = DNSRecordUtils.createARecord("example2.domain.com", 86400L, "127.0.0.3"); 
		DNSRecord record4 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example.domain.com", 86400L, 3506, 1, 1); 
		DNSRecord record5 = DNSRecordUtils.createSRVRecord("_ldap_cerner._tcp.cerner.com", "example2.domain.com", 86400L, 3506, 1, 1);
		dnsRepo.saveAll(Arrays.asList(record1, record2, record3, record4, record5));

		assertEquals(5, dnsRepo.count());
		
		// remove the first three records
		dnsRepo.deleteAll(Arrays.asList(record1, record2, record3));
		
		
		Collection<DNSRecord> records = dnsRepo.findAll();
		assertEquals(2, records.size());
		assertTrue(records.contains(record4));
		assertTrue(records.contains(record5));
		
		// remove the last two records
		dnsRepo.deleteAll(Arrays.asList(record4, record5));
		
		records = dnsRepo.findAll();
		assertEquals(0, records.size());

	}		
	
	
	@Test
	public void testRemoveByIds_noqualifying() throws Exception 
	{
		assertEquals(0, dnsRepo.count());
		
		try
		{
			dnsRepo.deleteById(876343L);
		}
		catch (EmptyResultDataAccessException e)
		{
			
		}
		// should result in a functional no-op
		assertEquals(0, dnsRepo.count());
	}		
	
	
	@Test
	public void testRemoveByRecords_noqualifying() throws Exception 
	{
		assertEquals(0, dnsRepo.count());
		
		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		
		// should result in a functional no-op
		dnsRepo.delete(record1);
		
		assertEquals(0, dnsRepo.count());

	}			
	
	@Test
	public void testUpdateRecord() throws Exception 
	{
		assertEquals(0, dnsRepo.count());
		

		DNSRecord record1 = DNSRecordUtils.createMXRecord("example.domain.com", "127.0.0.1", 86400L, 1);
		dnsRepo.saveAll(Arrays.asList(record1));
		
		Collection<DNSRecord> records = dnsRepo.findAll();
		assertEquals(1, records.size());
		DNSRecord checkRecord = records.iterator().next();
		assertEquals(record1, checkRecord);
		
		checkRecord.setName("example2.domain.com.");
		dnsRepo.save(checkRecord);  
		
		records = dnsRepo.findAll();
		assertEquals(1, records.size());
		DNSRecord modRecord = records.iterator().next();
		assertEquals(checkRecord, modRecord);
		
	}
	
	@Test
	public void testUpdateRecord_recordDoesNotExist() throws Exception 
	{
		
		/**
		 *  Move up to biz logic
		 **/ 
		/*
		assertEquals(0, dnsRepo.count());
		

		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		boolean exceptionOccured = false;
		try
		{
			dnsRepo.save(record1);
		}
		catch(Exception e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		*/
	}	
	
	
	@Test
	public void testUpdateRecord_illegalAnyType() throws Exception 
	{
		/**
		 *  Move up to biz logic
		 **/ 
		/*
		assertEquals(0, dnsDao.count());
		

		DNSRecord record1 = DNSRecordUtils.createARecord("example.domain.com", 86400L, "127.0.0.1");
		dnsDao.add(Arrays.asList(record1));
		
		Collection<DNSRecord> records = dnsDao.get(Type.ANY);
		assertEquals(1, records.size());
		DNSRecord checkRecord = records.iterator().next();
		assertEquals(record1, checkRecord);
		
		checkRecord.setType(Type.ANY);
		
		boolean exceptionOccured = false;
		try
		{
			dnsDao.update(checkRecord.getId(), checkRecord);
		}
		catch(ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		*/
		
	}
	
}
