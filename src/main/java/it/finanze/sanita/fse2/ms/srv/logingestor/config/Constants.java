/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.srv.logingestor.config;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

	
	public static final class Collections {

		public static final String LOG_CONTROL_COLLECTION_NAME = "log_collector";
		
		public static final String LOG_KPI_COLLECTION_NAME = "log_collector_kpi";

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
			public static final String OP_LOCALITY = "op_locality";
			public static final String OP_DOCUMENT_TYPE = "op_document_type";
			public static final String MICROSERVICE_NAME = "microservice_name";
			public static final String OP_ROLE = "op_role";
			public static final String OP_FISCAL_CODE = "op_fiscal_code";
			public static final String GATEWAY_NAME = "gateway_name";
			public static final String OP_WARNING = "op_warning";
			public static final String OP_WARNING_DESCRIPTION = "op_warning_description";
			public static final String OP_SUBJ_APPLICATION = "op_subj_application";
			public static final String OP_SUBJ_APPLICATION_ID = "op_application_id";
			public static final String OP_SUBJ_APPLICATION_VENDOR = "op_application_vendor";
			public static final String OP_SUBJ_APPLICATION_VERSION = "op_application_version";
			public static final String WORKFLOW_INSTANCE_ID = "workflow_instance_id";
			public static final String TYPE_ID_EXTENSION = "typeIdExtension";
			public static final String ADMINISTRATIVE_REQUEST = "administrative_request";
			public static final String AUTHOR_INSTITUTION = "author_institution";
			public static final String ID_DOCUMENTO = "id_documento";
			
			public static final String LOG_TYPE_KPI = "kpi-structured-log";
			public static final String LOG_TYPE_CONTROL = "control-structured-log";
			
		}
	}
  
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Properties {
		public static final String MS_NAME = "srv-log-ingestion";
	}

}
