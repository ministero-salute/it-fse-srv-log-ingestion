# Fascicolo Sanitario 2.0

# _it-fse-srv-log-ingestion_

Il microservizio in oggetto ha la responsabilità di accogliere i log strutturati provenienti dal Gateway.

<br/>

## Repos
- [*it-fse-support*](https://github.com/ministero-salute/it-fse-support) - Pagina di supporto FSE 2.0
- [*it-fse-landing*](https://github.com/ministero-salute/it-fse-landing) - Landing page dei repository FSE 2.0

<br/>

## Test ISOLATION

Con JDK 21 e Maven, eseguire i test senza MongoDB, Kafka, container o servizi esterni:

```sh
mvn -PISOLATION clean test
```

Il profilo seleziona esclusivamente i test JUnit con tag `ISOLATION`, esclusi dalla
normale esecuzione `mvn test`. I contesti Spring minimali usano il profilo Spring
`ISOLATION` e registrano solo i componenti necessari, senza avviare l'applicazione.
I test verificano controller HTTP, servizi, mapping e query MongoDB, listener Kafka,
configurazione, health check e utility; i confini esterni sono simulati con Mockito.

Report JaCoCo HTML:
`target/site/jacoco/index.html`; XML: `target/site/jacoco/jacoco.xml`.

## License

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL_v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
