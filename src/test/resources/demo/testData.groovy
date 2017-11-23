import de.triology.blog.testdata.loader.it.Department
import de.triology.blog.testdata.loader.it.User

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
