package it.finanze.sanita.fse2.ms.srv.logingestor.config;

/**
 * 
 * @author vincenzoingenito
 *
 * Constants application.
 */
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
		public static final String REPOSITORY_MONGO = "it.sanita.logingestor.repository";

		public static final class Collections {

			public static final String TRANSACTION_DATA = "transaction_data";

			private Collections() {

			}
		}
		
		private ComponentScan() {
			//This method is intentionally left blank.
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
		 * Constructor.
		 */
		private Profile() {
			//This method is intentionally left blank.
		}

	}
  
	/**
	 *	Constants.
	 */
	private Constants() {

	}

}
