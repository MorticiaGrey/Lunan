if not ((tableLength(args)) == 2) then
    print("usage: login [USERNAME] [PASSWORD]")
    return
end

if not lunan.setCurrUser(args[1], args[2]) then
    print("error: invalid username or password")
end
