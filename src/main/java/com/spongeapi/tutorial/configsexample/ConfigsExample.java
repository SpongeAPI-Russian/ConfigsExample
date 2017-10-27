package com.spongeapi.tutorial.configsexample;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.util.*;

@Plugin(
        id = "configsexample",
        name = "ConfigsExample",
        version = "0.1-SNAPSHOT",
        url = "https://spongeapi.com",
        authors = {
                "Xakep_SDK"
        }
)
public class ConfigsExample {

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> localLoader;

    @Inject
    private GuiceObjectMapperFactory factory;

    @Listener
    public void onServerStart(GameStartedServerEvent event) throws IOException, ObjectMappingException {
        // Просто загрузить и установить какую-то ноду, сохранить
        CommentedConfigurationNode defaultWayNode = localLoader.load();
        defaultWayNode.getNode("test", "node").setValue(true);
        logger.info(String.valueOf(defaultWayNode.getNode("test", "node").getValue()));
        localLoader.save(defaultWayNode);
        /////////////////////////////////////////////////////////////////////////////////////////////////

        // Установить список, сохранить, получить.
        List<String> strings = new ArrayList<>();
        strings.add("Sanya");
        strings.add("Dima");
        strings.add("Sergey");
        strings.add("Sasha");
        strings.add("Nastya");
        strings.add("Olya");

        defaultWayNode.getNode("names").setValue(strings);
        localLoader.save(defaultWayNode);
        logger.info(String.valueOf(defaultWayNode.getNode("names").getList(TypeToken.of(String.class))));
        /////////////////////////////////////////////////////////////////////////////////////////////////

        // Установить ItemStack с зачарованиями, сохранить, получить.
        ItemStack sword = ItemStack.of(ItemTypes.DIAMOND_SWORD, 1);
        EnchantmentData enchantmentData = sword.getOrCreate(EnchantmentData.class).get();
        enchantmentData.addElement(new ItemEnchantment(Enchantments.FIRE_ASPECT, 1)); // API7 ItemEnchantment.of(Enchantment,int), API6 - new ItemEnchantment(Enchantment,int)
        enchantmentData.addElement(new ItemEnchantment(Enchantments.SHARPNESS, 1000));
        sword.offer(enchantmentData);

        defaultWayNode.getNode("mySword").setValue(TypeToken.of(ItemStack.class), sword);
        localLoader.save(defaultWayNode);

        ItemStack mySword = defaultWayNode.getNode("mySword").getValue(TypeToken.of(ItemStack.class));
        logger.info(String.valueOf(mySword.require(EnchantmentData.class)));
        /////////////////////////////////////////////////////////////////////////////////////////////////

        // Пример с сериализатором
        // глобальная регистрация(так не делать, если не знаешь, зачем надо)
        //TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(MyObject.class), new MyObjectSerializer());
        TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
        serializers.registerType(TypeToken.of(MyObject.class), new MyObjectSerializer());
        ConfigurationOptions opts = ConfigurationOptions.defaults().setSerializers(serializers);

        CommentedConfigurationNode customSerializerNode = localLoader.load(opts);

        MyObject object = new MyObject("Какая-то строка", 7, sword);
        customSerializerNode.getNode("mySimpleObject").setValue(TypeToken.of(MyObject.class), object);

        MyObject mySimpleObject = customSerializerNode.getNode("mySimpleObject").getValue(TypeToken.of(MyObject.class));
        logger.info(String.valueOf(mySimpleObject));
        /////////////////////////////////////////////////////////////////////////////////////////////////

        // Пример с аннотациями и кастомным ObjectMapperFactory.
        // Для тех, кто пришел со страницы с туториалом: ObjectMapperFactory можно не заменять, это нужно для того,
        // чтобы можно было использовать инжекцию в POJO.
        CommentedConfigurationNode guiceMapperNode = localLoader.load(opts.setObjectMapperFactory(factory));

        ItemStack copy = sword.copy();
        copy.offer(Keys.DISPLAY_NAME, Text.of("Супер-меч!"));

        MyEnhancedObject meo = new MyEnhancedObject(mySimpleObject, copy, UUID.randomUUID());
        guiceMapperNode.getNode("proWayCoding").setValue(TypeToken.of(MyEnhancedObject.class), meo);

        MyEnhancedObject myEnhancedObject = guiceMapperNode.getNode("proWayCoding").getValue(TypeToken.of(MyEnhancedObject.class));
        logger.info(String.valueOf(myEnhancedObject));

        localLoader.save(guiceMapperNode);
        /////////////////////////////////////////////////////////////////////////////////////////////////

        // Пример с сериализатором и Set<?>
        ConfigurationOptions setOpts = opts.setSerializers(serializers.registerType(new TypeToken<Set<?>>() {}, new SetSerializer()));
        CommentedConfigurationNode setNode = localLoader.load(setOpts);
        Set<MyObject> myObjects = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            MyObject obj1 = new MyObject("string" + i, i, ItemStack.of(ItemTypes.LOG, i));
            myObjects.add(obj1);
        }
        setNode.getNode("mySet").setValue(new TypeToken<Set<MyObject>>() {}, myObjects);

        Set<MyObject> mySet = setNode.getNode("mySet").getValue(new TypeToken<Set<MyObject>>() {});
        logger.info(String.valueOf(mySet));

        localLoader.save(setNode);
        /////////////////////////////////////////////////////////////////////////////////////////////////

        // Пример с сериализатором и List<MyObject>
        CommentedConfigurationNode listNode = localLoader.load(opts);
        List<MyObject> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            MyObject obj1 = new MyObject("string" + i, i, ItemStack.of(ItemTypes.LOG, i));
            list.add(obj1);
        }
        setNode.getNode("myList").setValue(new TypeToken<List<MyObject>>() {}, list);

        List<MyObject> myList = listNode.getNode("myList").getList(TypeToken.of(MyObject.class));
        logger.info(String.valueOf(myList));

        localLoader.save(setNode);
        /////////////////////////////////////////////////////////////////////////////////////////////////
    }

}
