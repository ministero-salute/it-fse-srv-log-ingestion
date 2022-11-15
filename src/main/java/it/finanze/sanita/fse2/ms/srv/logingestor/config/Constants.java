/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.config;


public final class Constants {

    /**
	 *	Path scan.
	 */
	public static final class ComponentScan {

		/**
		 * Base path.
		 */
		public static final String BASE = "it.finanze.sanita.fse2.ms.srv.logingestor";

		/**
		 * Controller path.
		 */
		public static final String CONTROLLER = "it.finanze.sanita.fse2.ms.srv.logingestor.controller";

		/**
		 * Configuration path.
		 */
		public static final String CONFIG = "it.finanze.sanita.fse2.ms.srv.logingestor.config";
		 
		/**
		 * Configuration mongo path.
		 */
		public static final String CONFIG_MONGO = "it.finanze.sanita.fse2.ms.srv.logingestor.config.mongo";

		/**
		 * Configuration mongo repository path.
		 */
		public static final String REPOSITORY_MONGO = "it.finanze.sanita.fse2.ms.srv.logingestor.repository";
		

		
		private ComponentScan() {
			//This method is intentionally left blank.
		}

	}
	
	public static final class Collections {

		public static final String LOG_COLLECTION_NAME = "log_collector";

		private Collections() {

		}
	}
 
	
	public static final class Profile {
		public static final String TEST = "test";
		public static final String TEST_PREFIX = "test_";

		/**
		 * Dev profile.
		 */
		public static final String DEV = "dev";
		
		/**
		 * Docker profile.
		 */
		public static final String DOCKER = "docker";

		/** 
		 * Constructor.
		 */
		private Profile() {
			//This method is intentionally left blank.
		}

	}

	public static class App {
		private App() {}

		public static class Regex {
			private Regex() {}
			public static final String NUM_REGEX = "\\d{3}";
		}

		public static class Custom {
			private Custom() {}
			public static final String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss.SSS";
		}
	}

	public static class Mongo {
		public static class Query {
			private Query() {}
			public static final String REGION = "op_issuer.region";
		}

		private Mongo() {}

		public static class Fields {
			public static final String ID = "_id";

			private Fields() {}
			public static final String LOG_TYPE = "log_type";
			public static final String MESSAGE = "message";
			public static final String OPERATION = "operation";
			public static final String OP_RESULT = "op_result";
			public static final String OP_TIMESTAMP_START = "op_timestamp_start";
			public static final String OP_TIMESTAMP_END = "op_timestamp_end";
			public static final String OP_ERROR = "op_error";
			public static final String OP_ERROR_DESCRIPTION = "op_error_description";
			public static final String OP_ISSUER = "op_issuer";
			public static final String OP_DOCUMENT_TYPE = "op_document_type";
			public static final String MICROSERVICE_NAME = "microservice_name";
			public static final String OP_ROLE = "op_role";
			public static final String OP_FISCAL_CODE = "op_fiscal_code";
			public static final String GATEWAY_NAME = "gateway_name";
		}
	}
  
	/**
	 *	Constants.
	 */
	private Constants() {

	}

}
