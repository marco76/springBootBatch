package ch.javaee.springBootBatch;

import ch.javaee.springBootBatch.model.Person;
import ch.javaee.springBootBatch.processor.PersonItemProcessor;
import ch.javaee.springBootBatch.tokenizer.PersonFixedLineTokenizer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;


@Configuration

@EnableAutoConfiguration
@EnableBatchProcessing
@ComponentScan
@PropertySource("classpath:application.properties")
public class BatchConfiguration {


    /**
     * We define a bean that read each line of the input file.
     *
     * @return
     */
    @Bean
    public ItemReader<Person> reader() {
        // we read a flat file that will be used to fill a Person object
        FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();
        // we pass as parameter the flat file directory
        reader.setResource(new FileSystemResource(new File("/Users/marco/Documents/PersonData.txt")));
        // we use a default line mapper to assign the content of each line to the Person object
        reader.setLineMapper(new DefaultLineMapper<Person>() {{
            // we use a custom fixed line tokenizer
            setLineTokenizer(new PersonFixedLineTokenizer());
            // as field mapper we use the name of the fields in the Person class
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                // we create an object Person
                setTargetType(Person.class);
            }});
        }});
        return reader;
    }

    /**
     * The ItemProcessor is called after a new line is read and it allows the developer
     * to transform the data read
     *
     * @return
     */
    @Bean
    public ItemProcessor<Person, Person> processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public ItemWriter<Person> writer() {
        JpaItemWriter writer = new JpaItemWriter<Person>();
        writer.setEntityManagerFactory(entityManagerFactory().getObject());

        return writer;
    }


    @Bean
    public Job importPerson(JobBuilderFactory jobs, Step s1) {

        return jobs.get("import")
                .incrementer(new RunIdIncrementer())
                .flow(s1)
                .end()
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<Person> reader,
                      ItemWriter<Person> writer, ItemProcessor<Person, Person> processor) {
        return stepBuilderFactory.get("step1")
                .<Person, Person>chunk(1000)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/BatchDB");
        dataSource.setUsername("root");
        dataSource.setPassword("");
        return dataSource;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setPackagesToScan("ch.javaee.springBootBatch");
        lef.setDataSource(dataSource());
        lef.setJpaVendorAdapter(jpaVendorAdapter());
        lef.setJpaProperties(getJpaProperties());
        return lef;
    }

    @Bean
    public EntityManager entityManger() {
        return entityManagerFactory().getObject().createEntityManager();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }

    private Properties getJpaProperties() {
        return new Properties() {
            {
                setProperty("hibernate.hbm2ddl.auto", "update");
                //              setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
                setProperty("hibernate.show_sql", "false");
                setProperty("show_sql", "false");
                // setProperty("hibernate.format_sql", "true");
            }
        };
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        jpaVendorAdapter.setGenerateDdl(true);
        jpaVendorAdapter.setShowSql(false);

        jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
        return jpaVendorAdapter;
    }

}
