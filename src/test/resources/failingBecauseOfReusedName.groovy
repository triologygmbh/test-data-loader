import de.triology.blog.testdataloader.testentities.AnotherTestEntity
import de.triology.blog.testdataloader.testentities.BasicTestEntity

import static de.triology.blog.testdataloader.EntityBuilder.create

create BasicTestEntity, 'nameThatIsUsedTwoTimes', {}
create AnotherTestEntity, 'nameThatIsUsedTwoTimes', {}
