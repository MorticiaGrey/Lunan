if args[1] == nil then
    print("usage: passwd [USER_NAME]")
    return
end

if not lunan.userExists(args[1]) then
    print("error: user [" .. args[1] .. "] not found")
    return
end

local password = lunan.read("Enter new password: ")
local password2 = lunan.read("Retype new password: ")

if not (password == password2) then
    print("error: passwords do not match")
    return
end

if not lunan.setUserPassword(args[1], password) then
    print("error: insufficient permissions")
end
