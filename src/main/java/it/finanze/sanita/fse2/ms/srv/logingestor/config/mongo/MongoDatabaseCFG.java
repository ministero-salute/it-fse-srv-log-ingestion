package it.finanze.sanita.fse2.ms.srv.logingestor.config.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import it.finanze.sanita.fse2.ms.srv.logingestor.config.Constants;

@Configuration
@EnableMongoRepositories(basePackages = Constants.ComponentScan.CONFIG_MONGO)
public class MongoDatabaseCFG {

	@Autowired
	private MongoPropertiesCFG mongoPropertiesCFG;

    @Autowired
    private ApplicationContext appContext;

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(){
        return new SimpleMongoClientDatabaseFactory(mongoPropertiesCFG.getUri());
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate() {
        final MongoDatabaseFactory factory = mongoDatabaseFactory();

        final MongoMappingContext mongoMappingContext = new MongoMappingContext();
        mongoMappingContext.setApplicationContext(appContext);
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(factory), mongoMappingContext);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(factory, converter);
    }

}
