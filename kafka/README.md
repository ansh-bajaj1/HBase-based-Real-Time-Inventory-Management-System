# Kafka Notes

- Topic used by the system: `inventory-updates`
- Producer location: backend service (`InventoryEventProducer`)
- Consumer location: backend service (`InventoryEventConsumer`)

You can inspect events with:

```bash
docker exec -it kafka kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic inventory-updates --from-beginning
```
