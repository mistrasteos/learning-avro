import com.redis.testcontainers.RedisContainer;
import mistrasteos.learning.avro.domain.hospital.Patient;
import mistrasteos.learning.avro.utils.Util;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;

import java.io.IOException;

public class SerializerTest {

    final Patient patient1 = Patient.newBuilder()
            .setId(1)
            .setName("Art Vandelay")
            .build();

    @Test
    void simpleSerializationTest() throws IOException {
        byte[] patient1Serialized = Util.serialize(patient1);
        Patient retrievedPatient = Util.deserialize( patient1Serialized, Patient.getClassSchema() );

        Assertions.assertThat( retrievedPatient ).isEqualByComparingTo( patient1 );
    }

    @Test
    void externalSystemSerialization() throws Exception {
        try(RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7"))){
            redis.start();

            byte[] patient1Serialized = Util.serialize(patient1);

            try(Jedis jedis = new Jedis(redis.getRedisHost(), redis.getRedisPort())){
                // write to Redis
                byte[] key = Util.serialize( "patient:" + patient1.getId() );
                jedis.set(key, patient1Serialized);

                // retrieve from Redis
                byte[] result = jedis.get(key);
                Patient retrievedPatient = Util.deserialize(result, Patient.getClassSchema());

                Assertions.assertThat( retrievedPatient ).isEqualByComparingTo( patient1 );

                redis.stop();
            }
        }
    }

}//class
