package mistrasteos.learning.avro.utils;

import org.apache.avro.Schema;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Util {

    /*
    // complete generic
    public static <T extends SpecificRecord> byte[] serialize(T object) throws IOException {
        DatumWriter<T> objectWriter = new SpecificDatumWriter<T>(object.getSchema());

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            objectWriter.write(object, encoder);
            encoder.flush();
            return outputStream.toByteArray();
        }
    }
    */

    public static <T> byte[] serialize(T object, Schema schema) throws IOException {
        DatumWriter<T> objectWriter = new SpecificDatumWriter<T>(schema);

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            objectWriter.write(object, encoder);
            encoder.flush();
            return outputStream.toByteArray();
        }
    }

    public static <T extends SpecificRecord> byte[] serialize(T object) throws IOException {
        return serialize(object, object.getSchema());
    }

    // To serialize just Strings, for example a Redis key
    public static byte[] serialize(String string) throws IOException{
        return serialize(string, Schema.create(Schema.Type.STRING));
    }

    public static <T extends SpecificRecord> T deserialize(byte[] bytes, Schema schema) throws IOException{
        DatumReader<T> objectReader = new SpecificDatumReader<T>(  schema );

        try(ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)){
            Decoder decoder = DecoderFactory.get().binaryDecoder(inputStream, null);
            return objectReader.read(null, decoder);
        }
    }

}
