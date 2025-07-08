package in.ind.pkg.usermanager.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.avro.AvroFactory;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;
import com.fasterxml.jackson.dataformat.avro.schema.AvroSchemaGenerator;
import in.ind.pkg.usermanager.config.MultiTenantConfig;
import in.ind.pkg.usermanager.model.User;
import in.ind.pkg.usermanager.repository.UserRepository;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.hadoop.util.HadoopOutputFile;
import org.apache.parquet.avro.AvroParquetWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class UserExportScheduler {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

    private static final List<String> tenantList = List.of("tenant1", "tenant2"); // optionally configurable

    @Autowired
    private UserRepository userRepository;

    @Scheduled(fixedRate = 5 * 60 * 1000) // every 5 minutes
    public void exportUsersToParquet() {
        for (String tenant : tenantList) {
            try {
                MultiTenantConfig.setCurrentTenant(tenant);
                List<User> users = userRepository.findAll();
                if (users.isEmpty()) continue;

                writeParquet(users, tenant);
            } catch (Exception e) {
                System.err.println("Export failed for tenant: " + tenant + " -> " + e.getMessage());
            } finally {
                MultiTenantConfig.clear();
            }
        }
    }

    private void writeParquet(List<User> users, String tenantId) throws IOException {
        String timestamp = LocalDateTime.now().format(formatter);
        String fileName = String.format("user_%s_%s.parquet", tenantId, timestamp);
        File file = Paths.get(System.getProperty("user.home"), fileName).toFile();

        ObjectMapper mapper = new ObjectMapper(new AvroFactory());
        AvroSchemaGenerator gen = new AvroSchemaGenerator();
        mapper.acceptJsonFormatVisitor(User.class, gen);
        AvroSchema avroSchema = gen.getGeneratedSchema();

        try (ParquetWriter<Object> writer = AvroParquetWriterBuilder
                .withFile(file)
                .withSchema(avroSchema.getAvroSchema())
                .build()) {
            for (User user : users) {
                writer.write(user);
            }
        }
    }

    private static class AvroParquetWriterBuilder {
        // Removed ambiguous builder(null) method.
        // Use withFile(File) to start the builder chain.
        public static Builder withFile(File file) throws IOException {
            return new Builder(file);
        }

        public static class Builder {
            private final File file;
            private org.apache.avro.Schema schema;

            public Builder(File file) {
                this.file = file;
            }

            public Builder withSchema(org.apache.avro.Schema schema) {
                this.schema = schema;
                return this;
            }

            public ParquetWriter<Object> build() throws IOException {
                return AvroParquetWriter.<Object>builder(HadoopOutputFile.fromPath(
                                new org.apache.hadoop.fs.Path(file.getAbsolutePath()),
                                new org.apache.hadoop.conf.Configuration()))
                        .withSchema(schema)
                        .withCompressionCodec(CompressionCodecName.SNAPPY)
                        .build();
            }
        }
    }
}
