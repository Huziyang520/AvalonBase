**English** | [中文](#中文)

---

# Config and networking

## Config file IO — `config/AvalonToml`

Reads and writes TOML config files, so your mod only keeps plain static fields.

| Member | Purpose |
|---|---|
| `new AvalonToml(modId, path)` | Create a config handle for a file |
| `ensureExists(defaultConfig)` | Create the file if it is missing or empty |
| `read()` / `readTracked()` | Read the file (`readTracked` also remembers the timestamp) |
| `write(cfg)` / `write(cfg, sectionMarker, comment)` | Write the file, optionally injecting a comment before a marker |
| `isChanged()` | Has the file been modified since the last tracked read? |
| `AvalonToml.getSubList(cfg, section)` / `setSubList(cfg, section, list)` | Read/write a string list in a section (the sub-key is `blacklist`) |
| `AvalonToml.insertComment(toml, marker, comment)` | Insert a comment into a TOML string |

```java
AvalonToml toml = new AvalonToml("mymod", Paths.get("config", "mymod.toml"));

toml.ensureExists(defaultConfig);          // create the file on first run
Config cfg = toml.readTracked();           // read it
String mode = cfg.getOrElse("mode", "disabled");
myList = AvalonToml.getSubList(cfg, "my_section");

toml.write(cfg, "[my_section]", "# available: a, b, c\n");   // write it back with a hint
if (toml.isChanged()) { /* the file was edited outside the game */ }
```

## Networking — `network/AvalonNetwork`

One platform-independent facade for client ⇄ server messages. Your mod declares a channel id, a message class, an encoder, a decoder and a handler; the loader modules provide the implementation.

| Member | Purpose |
|---|---|
| `registerMessage(channel, clazz, encoder, decoder, handler)` | Register one message type on its channel |
| `sendToServer(channel, message)` | Client → server |
| `sendToAll(server, channel, message)` | Server → every client |
| `sendToPlayer(player, channel, message)` | Server → one client |
| `MessageContext#getMessage()` | The received message |
| `MessageContext#enqueueWork(Runnable)` | Run on the game thread |
| `MessageContext#getSender()` | Sending `ServerPlayer` (null on the client side) |
| `MessageContext#isClientSide()` | Which side received it |

```java
Identifier UPDATE = Identifier.fromNamespaceAndPath("mymod", "update");
Identifier SYNC   = Identifier.fromNamespaceAndPath("mymod", "sync");

// register (once, during mod init)
AvalonNetwork.registerMessage(UPDATE, MyUpdatePacket.class,
        MyUpdatePacket::encode, MyUpdatePacket::new,
        ctx -> ctx.enqueueWork(() -> {
            if (ctx.getSender() != null) ctx.getMessage().applyToServer(ctx.getSender());
        }));

AvalonNetwork.registerMessage(SYNC, MySyncPacket.class,
        MySyncPacket::encode, MySyncPacket::new,
        ctx -> ctx.enqueueWork(() -> {
            if (ctx.isClientSide()) ctx.getMessage().applyToClient();
        }));

// send
AvalonNetwork.sendToServer(UPDATE, new MyUpdatePacket(...));       // from the editor screen
AvalonNetwork.sendToPlayer(player, SYNC, new MySyncPacket(...));   // on join
AvalonNetwork.sendToAll(server, SYNC, new MySyncPacket(...));      // broadcast
```

Messages are plain classes with `encode(FriendlyByteBuf)` and a `FriendlyByteBuf` constructor.

## Platform services — `platform/Services`

| Member | Purpose |
|---|---|
| `Services.PLATFORM.getPlatformName()` | Current loader name |
| `Services.PLATFORM.isModLoaded(modId)` | Is that mod present? |
| `Services.PLATFORM.isDevelopmentEnvironment()` | Dev environment? |
| `Services.PLATFORM.getEnvironmentName()` | `"development"` / `"production"` |

```java
if (Services.PLATFORM.isModLoaded("avalonbase")) { ... }
```

The platform modules supply their own `IPlatformHelper` through `META-INF/services`, so this call works the same on every loader.

---
---

<a id="中文"></a>

[English](#config-and-networking) | **中文**

---

# 配置与网络

## 配置文件读写 —— `config/AvalonToml`

负责 TOML 配置文件的读写，业务模组只需要保留自己的静态字段。

| 成员 | 作用 |
|---|---|
| `new AvalonToml(modId, path)` | 为一个文件创建配置句柄 |
| `ensureExists(defaultConfig)` | 文件不存在或为空时创建它 |
| `read()` / `readTracked()` | 读取文件（`readTracked` 同时记住时间戳） |
| `write(cfg)` / `write(cfg, sectionMarker, comment)` | 写盘，可在指定标记前注入一行注释 |
| `isChanged()` | 自上次记录以来文件是否被改过 |
| `AvalonToml.getSubList(cfg, section)` / `setSubList(cfg, section, list)` | 读写某小节里的字符串列表（子键为 `blacklist`） |
| `AvalonToml.insertComment(toml, marker, comment)` | 往 TOML 文本里插入注释 |

```java
AvalonToml toml = new AvalonToml("mymod", Paths.get("config", "mymod.toml"));

toml.ensureExists(defaultConfig);          // 首次运行创建文件
Config cfg = toml.readTracked();           // 读取
String mode = cfg.getOrElse("mode", "disabled");
myList = AvalonToml.getSubList(cfg, "my_section");

toml.write(cfg, "[my_section]", "# 可选值：a, b, c\n");   // 写回并附说明
if (toml.isChanged()) { /* 文件被游戏外改过 */ }
```

## 网络 —— `network/AvalonNetwork`

一套与平台无关的客户端 ⇄ 服务端消息门面。业务模组只声明通道 id、消息类、编码器、解码器与处理回调；实现由各加载器模块提供。

| 成员 | 作用 |
|---|---|
| `registerMessage(channel, clazz, encoder, decoder, handler)` | 在某通道注册一种消息 |
| `sendToServer(channel, message)` | 客户端 → 服务端 |
| `sendToAll(server, channel, message)` | 服务端 → 全部客户端 |
| `sendToPlayer(player, channel, message)` | 服务端 → 单个客户端 |
| `MessageContext#getMessage()` | 收到的消息 |
| `MessageContext#enqueueWork(Runnable)` | 在游戏线程上执行 |
| `MessageContext#getSender()` | 发送方 `ServerPlayer`（客户端侧为 null） |
| `MessageContext#isClientSide()` | 由哪一侧收到 |

```java
Identifier UPDATE = Identifier.fromNamespaceAndPath("mymod", "update");
Identifier SYNC   = Identifier.fromNamespaceAndPath("mymod", "sync");

// 注册（模组初始化时做一次）
AvalonNetwork.registerMessage(UPDATE, MyUpdatePacket.class,
        MyUpdatePacket::encode, MyUpdatePacket::new,
        ctx -> ctx.enqueueWork(() -> {
            if (ctx.getSender() != null) ctx.getMessage().applyToServer(ctx.getSender());
        }));

AvalonNetwork.registerMessage(SYNC, MySyncPacket.class,
        MySyncPacket::encode, MySyncPacket::new,
        ctx -> ctx.enqueueWork(() -> {
            if (ctx.isClientSide()) ctx.getMessage().applyToClient();
        }));

// 发送
AvalonNetwork.sendToServer(UPDATE, new MyUpdatePacket(...));       // 编辑界面里
AvalonNetwork.sendToPlayer(player, SYNC, new MySyncPacket(...));   // 玩家加入时
AvalonNetwork.sendToAll(server, SYNC, new MySyncPacket(...));      // 广播
```

消息类就是普通类，提供 `encode(FriendlyByteBuf)` 与一个接收 `FriendlyByteBuf` 的构造器。

## 平台服务 —— `platform/Services`

| 成员 | 作用 |
|---|---|
| `Services.PLATFORM.getPlatformName()` | 当前加载器名 |
| `Services.PLATFORM.isModLoaded(modId)` | 某个模组装没装 |
| `Services.PLATFORM.isDevelopmentEnvironment()` | 是否开发环境 |
| `Services.PLATFORM.getEnvironmentName()` | `"development"` / `"production"` |

```java
if (Services.PLATFORM.isModLoaded("avalonbase")) { ... }
```

各加载器模块通过 `META-INF/services` 提供自己的 `IPlatformHelper`，所以这行调用在每个加载器上写法一致。
