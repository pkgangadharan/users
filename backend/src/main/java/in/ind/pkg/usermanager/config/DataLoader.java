package in.ind.pkg.usermanager.config;

import in.ind.pkg.usermanager.model.User;
import in.ind.pkg.usermanager.repository.UserRepository;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:parquet/*.parquet");

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null || !filename.startsWith("user_")) continue;

                String tenantId = filename.split("_")[1];
                MultiTenantConfig.setCurrentTenant(tenantId);
                List<User> users = readParquet(resource);
                userRepository.saveAll(users);
                MultiTenantConfig.clear();
            }
        } catch (Exception e) {
            System.err.println("Startup data load failed: " + e.getMessage());
        }
    }

    private List<User> readParquet(Resource resource) throws Exception {
        List<User> users = new ArrayList<>();

        try (InputStream in = resource.getInputStream()) {
            Configuration conf = new Configuration();
            ParquetReader<Group> reader = ParquetReader
                    .builder(new GroupReadSupport(), new Path(resource.getURI()))
                    .withConf(conf)
                    .build();

            Group group;
            while ((group = reader.read()) != null) {
                User user = new User();
                user.setId(group.getLong("id", 0));
                user.setFirstName(group.getString("firstName", 0));
                user.setLastName(group.getString("lastName", 0));
                user.setEmail(group.getString("email", 0));
                user.setDateOfBirth(LocalDate.parse(group.getString("dateOfBirth", 0)));
                users.add(user);
            }
        }

        return users;
    }
}
