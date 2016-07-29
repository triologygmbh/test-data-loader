import de.triology.blog.testdataloader.entities.Department

import static de.triology.blog.testdataloader.EntityBuilder.create
import de.triology.blog.testdataloader.entities.User

create User, 'Peter', {
    id = 123
    firstName = 'Peter'
    lastName = 'Pan'
    login = 'pete'
    email = 'peter.pan@example.com'
    department = create Department, 'lostBoys', {
        id = 999
        name = 'The Lost Boys'
        head = Peter
    }
}

create User, 'Tinker', {
    id = 555
    firstName = 'Tinker'
    lastName = 'Bell'
    department = lostBoys
}

create User, 'James', {
    id = 987
    firstName = 'James'
    lastName = 'Hook'
    login = 'CaptainHook'
    email = 'james.hook@example.com'
}
