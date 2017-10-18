package com.spongeapi.tutorial.configsexample;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.item.inventory.ItemStack;

public class MyObjectSerializer implements TypeSerializer<MyObject> {
    @Override
    public MyObject deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String someString = value.getNode("someString").getString();
        int someInt = value.getNode("someInt").getInt();
        ItemStack someStack = value.getNode("someStack").getValue(TypeToken.of(ItemStack.class));
        return new MyObject(someString, someInt, someStack);
    }

    @Override
    public void serialize(TypeToken<?> type, MyObject obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("someString").setValue(obj.getSomeString());
        value.getNode("someInt").setValue(obj.getSomeInt());
        value.getNode("someStack").setValue(TypeToken.of(ItemStack.class), obj.getSomeStack());
    }
}
