package net.nelz.simplesm.aop;

import net.nelz.simplesm.annotations.*;
import net.nelz.simplesm.exceptions.*;
import org.apache.commons.lang.*;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.*;

import java.security.*;
import java.lang.reflect.*;

/**
 * Copyright 2008 Widgetbox, Inc.
 * All rights reserved.
 * THIS PROGRAM IS CONFIDENTIAL AND AN UNPUBLISHED WORK AND TRADE
 * SECRET OF THE COPYRIGHT HOLDER, AND DISTRIBUTED ONLY UNDER RESTRICTION.
 */
public class CacheBaseTest {
	private CacheBase cut;

	@BeforeClass
	public void beforeClass() {
		cut = new CacheBase();

		cut.setMethodStore(new CacheKeyMethodStoreImpl());
	}

	@Test
	public void testKeyMethodArgs() throws Exception {
		try {
			cut.getKeyMethod(new KeyObject01());
			fail("Expected exception.");
		} catch (InvalidAnnotationException ex) {
			assertTrue(ex.getMessage().indexOf("0 arguments") != -1);
			System.out.println(ex.getMessage());
		}

		try {
			cut.getKeyMethod(new KeyObject02());
			fail("Expected exception.");
		} catch (InvalidAnnotationException ex) {
			assertTrue(ex.getMessage().indexOf("String") != -1);
			System.out.println(ex.getMessage());
		}

		try {
			cut.getKeyMethod(new KeyObject03());
			fail("Expected exception.");
		} catch (InvalidAnnotationException ex) {
			assertTrue(ex.getMessage().indexOf("String") != -1);
			System.out.println(ex.getMessage());
		}

		try {
			cut.getKeyMethod(new KeyObject04());
			fail("Expected exception.");
		} catch (InvalidAnnotationException ex) {
			assertTrue(ex.getMessage().indexOf("only one method") != -1);
			System.out.println(ex.getMessage());
		}

		assertEquals("doIt", cut.getKeyMethod(new KeyObject05()).getName());
		assertEquals("toString", cut.getKeyMethod(new KeyObject06(null)).getName());
	}

	@Test
	public void testBuildCacheKey() {
		try {
			cut.buildCacheKey(null, null);
			fail("Expected exception.");
		} catch (InvalidParameterException ex) {
			assertTrue(ex.getMessage().indexOf("at least 1 character") != -1);
			System.out.println(ex.getMessage());
		}

		try {
			cut.buildCacheKey("", null);
			fail("Expected exception.");
		} catch (InvalidParameterException ex) {
			assertTrue(ex.getMessage().indexOf("at least 1 character") != -1);
			System.out.println(ex.getMessage());
		}

		try {
			cut.buildCacheKey("a", null);
			fail("Expected exception.");
		} catch (InvalidParameterException ex) {
			assertTrue(ex.getMessage().indexOf("at least 1 character") != -1);
			System.out.println(ex.getMessage());
		}

		try {
			cut.buildCacheKey("a", "");
			fail("Expected exception.");
		} catch (InvalidParameterException ex) {
			assertTrue(ex.getMessage().indexOf("at least 1 character") != -1);
			System.out.println(ex.getMessage());
		}

		final String objectId = RandomStringUtils.randomAlphanumeric(20);
		final String namespace = RandomStringUtils.randomAlphanumeric(12);

		final String result = cut.buildCacheKey(objectId, namespace);

		assertTrue(result.indexOf(objectId) != -1);
		assertTrue(result.indexOf(namespace) != -1);
	}

	@Test
	public void testGenerateCacheKey() throws Exception {
		final Method method = KeyObject.class.getMethod("toString", null);

		try {
			cut.generateObjectId(method, new KeyObject(null));
			fail("Expected Exception.");
		} catch (RuntimeException ex) {
			assertTrue(ex.getMessage().indexOf("empty key value") != -1);
		}

		try {
			cut.generateObjectId(method, new KeyObject(""));
			fail("Expected Exception.");
		} catch (RuntimeException ex) {
			assertTrue(ex.getMessage().indexOf("empty key value") != -1);
		}

		final String result = "momma";
		assertEquals(result, cut.generateObjectId(method, new KeyObject(result)));
	}

	private static class KeyObject {
		private String result;
		private KeyObject(String result) { this.result = result;}
		public String toString() { return result; }
	}

	private static class KeyObject01 {
		@CacheKeyMethod
		public void doIt(final String nonsense) { }
	}

	private static class KeyObject02 {
		@CacheKeyMethod
		public void doIt() { }
	}

	private static class KeyObject03 {
		@CacheKeyMethod
		public Long doIt() { return null; }
	}

	private static class KeyObject04 {
		@CacheKeyMethod
		public String doIt() { return null; }
		@CacheKeyMethod
		public String doItAgain() { return null; }
	}

	private static class KeyObject05 {
		public static final String result = "shrimp";
		@CacheKeyMethod
		public String doIt() { return result; }
	}

	private static class KeyObject06 {
		private String result;
		private KeyObject06(String result) { this.result = result;}
		public String toString() { return result; }
	}
}
