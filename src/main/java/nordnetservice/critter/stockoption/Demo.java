package nordnetservice.critter.stockoption;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.DoubleNode;

import java.io.IOException;

public class Demo extends StdDeserializer<StockOptionPrice> {

    public Demo () {
        this(null);
    }

    public Demo (Class<?> vc) {
        super(vc);
    }

    @Override
    public StockOptionPrice deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);

        double id = (Double) ((DoubleNode) node.get("id")).numberValue();
        String itemName = node.get("itemName").asText();
        double userId = (Double) ((DoubleNode) node.get("createdBy")).numberValue();

        //return new Item(id, itemName, new User(userId, null));
        return new StockOptionPrice ();
    }
}
