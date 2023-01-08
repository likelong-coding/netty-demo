**优势**

- 池化思想 - 可以重用池中 ByteBuf 实例，更节约内存，减少内存溢出的可能
- 读写指针分离，不需要像 ByteBuffer 一样切换读写模式
- 可以自动扩容
- 支持链式调用，使用更流畅方便
- 很多地方体现零拷贝，例如 slice、duplicate、CompositeByteBuf
