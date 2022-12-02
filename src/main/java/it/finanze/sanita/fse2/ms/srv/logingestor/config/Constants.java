/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.config;


import java.util.regex.Pattern;

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

			public static final String ASL_REGEX = "^\\d{3}";
			public static final String PRIVATE_HOSPITAL = "^\\d{6}$";
			public static final String PRIVATE_SSN_HOSPITAL_REGEX = "^\\d{9}$";
			public static final String PRIVATE_DOCTOR = "^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$";
			public static final String MMG_PLS_REGEX = "^\\d{3}.*[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$";
			public static final String COMPLEX_STRUCTURE_REGEX = "(^\\d{3}.*[a-zA-Z0-9]+$)++";

			public static class Compiled {
				private Compiled() {}
				public static final Pattern COMPILED_ASL_REGEX = Pattern.compile(ASL_REGEX);
				public static final Pattern COMPILED_PRIVATE_HOSPITAL_REGEX = Pattern.compile(PRIVATE_HOSPITAL);
				public static final Pattern COMPILED_PRIVATE_SSN_HOSPITAL_REGEX = Pattern.compile(PRIVATE_SSN_HOSPITAL_REGEX);
				public static final Pattern COMPILED_PRIVATE_DOCTOR_REGEX = Pattern.compile(PRIVATE_DOCTOR);
				public static final Pattern COMPILED_MMG_PLS_REGEX = Pattern.compile(MMG_PLS_REGEX);
				public static final Pattern COMPILED_COMPLEX_STRUCTURE = Pattern.compile(COMPLEX_STRUCTURE_REGEX);
			}
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
			public static final String OP_WARNING = "op_warning";
			public static final String OP_WARNING_DESCRIPTION = "op_warning_description";
		}
	}
  
	/**
	 *	Constants.
	 */
	private Constants() {

	}

}
