if not (args[1] == nil) then
    if flags[1] == "-G" then
        if not lunan.createUser(args[1], "default") then
            print("error: user exists or insufficient permissions")
        end
        if not lunan.addUserGroup(args[2], args[1]) then
            print("usage: useradd -G [GROUP_NAME] [USER_NAME]")
        end
    else
        if not lunan.createUser(args[1], "default") then
            print("error: user exists or insufficient permissions")
        end
    end
else
    print("usage: useradd [OPTIONS] [USER_NAME]]")
end
