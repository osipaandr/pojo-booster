# pojo-booster

Intellij IDEA plugin for quick transformation from a POJO to entity or DTO.

---

### Transformation to entity means:
- adding class-level annotations `@Entity` and `@Table(name = '*CLASS_NAME*')`
- each field gets `@Column(name = "*FIELD_NAME*")` if doesn't have yet
- if class contains field with name that looks like id("id", "pid", "anyNameId", "myNamePid" etc.) than that field puts on the top and gets `@Id`

### Transformation to DTO means:
- adding `@JsonProperty("*FIELD_NAME*")` for each field if it's not annotated with `@JsonIgnore`

### Following operations are performed for both cases as well:
- replacing `java.sql.Timestamp` or `java.sql.Date` field types with `java.time.Instant`
- if lombok support is enabled in settings than all class accessors are replaced with lombok class-level annotations. `@AllArgsConstructor`, `@NoArgsConstructor`, `@Builder`, `@Setter`, `@Getter` and `@Accessors(chain = true)` for entity or `@EqualsAndHashCode`, `@Data` for DTO
- formatting code with blank lines around every field
