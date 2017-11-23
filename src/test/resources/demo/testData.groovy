/**
 * MIT License
 *
 * Copyright (c) 2016 TRIOLOGY GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import de.triology.blog.testdataloader.it.Department
import de.triology.blog.testdataloader.it.User

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
