if not ((tableLength(args)) == 1) then
    print("usage: login [USERNAME]")
    return
end

local password = lunan.read("password: ")

if not lunan.setCurrUser(args[1], password) then
    print("error: invalid username or password")
end
